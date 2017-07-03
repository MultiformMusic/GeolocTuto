package net.multiform_music.geoloctuto.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import net.multiform_music.geoloctuto.R;
import net.multiform_music.geoloctuto.RunningActivity;
import net.multiform_music.geoloctuto.bean.RunningStepBean;
import net.multiform_music.geoloctuto.bus.GpsBus;
import net.multiform_music.geoloctuto.helper.ParamHelper;
import net.multiform_music.geoloctuto.helper.RunningHelper;
import net.multiform_music.geoloctuto.helper.WakeLockHelper;
import net.multiform_music.geoloctuto.task.SaveRunnigStepTask;

import static net.multiform_music.geoloctuto.RunningActivity.runningStarted;

/**
 * Created by michel.dio on 27/04/2017.
 *
 * GPSService
 *
 * Classe de service de géolocalisation temps réel
 *
 * Ecriture sur bus GpsBus pour mise à jour dans DebugActivity (point sur map)
 *
 */


public class GPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    // objet permettant la localisation
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;


    // flag pour savoir si première localisation lors du running
    private boolean firstLocation = true;

    // sauvegarde de la location précèdente
    private Location previousLocation;

    // flag de calibration
    public static boolean calibration = true;

    // distance totale
    public static float distanceTotale;
    public static float velocityTotale;
    public static int nbrPointLocation;

    private static final String LOGSERVICE = "##### GPSService";

    @Override
    public void onCreate() {

        Log.i(LOGSERVICE, "onCreate");

        super.onCreate();
        buildGoogleApiClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(LOGSERVICE, "onStartCommand");

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        if (WakeLockHelper.cpuWakeLock == null) {
            PowerManager mgr = (PowerManager) this.getSystemService(POWER_SERVICE);
            WakeLockHelper.cpuWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RunningWakeLock");
            WakeLockHelper.cpuWakeLock.acquire();
        }

        Toast.makeText(getApplicationContext(), "Service GPSService Démarré", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }


    @Override
    public void onConnected(Bundle bundle) {

        Log.i(LOGSERVICE, "onConnected" + bundle);

        // démarrage de la localisation
        startLocationUpdate();
    }

    /**
     *
     *  Initialisation de l'objet de requête de localisation
     *
     */
    private void initLocationRequest() {

        Log.i(LOGSERVICE, "initLocationRequest");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // paramètrage du déplacement minimal détecté
        mLocationRequest.setSmallestDisplacement(ParamHelper.distanceMin);

    }

    /**
     *
     *  Démarrage de la localisation temps réel
     *
     */
    private void startLocationUpdate() {

        Log.i(LOGSERVICE, "startLocationUpdate");

        initLocationRequest();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     *
     *  Arrêt de la localisation temps réel
     *
     */
    private void stopLocationUpdate() {

        Log.i(LOGSERVICE, "stopLocationUpdate");

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    /**
     *
     *  Construction de l'objet google client API localisation
     *
     */
    protected synchronized void buildGoogleApiClient() {

        Log.i(LOGSERVICE, "buildGoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOGSERVICE, "onConnectionSuspended " + i);

    }

    @Override
    public void onLocationChanged(Location currentlocation) {

        Log.i(LOGSERVICE, "onLocationChanged");

        // si running démarré et calibrationactive => on calibre en détermination la localisation actuelle
        // utilisé dès qu'on arrive sur la RunningActivity
        if(!runningStarted && calibration) {

            if (currentlocation.getAccuracy() > ParamHelper.accuracy) {
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.running_calibration), Toast.LENGTH_SHORT).show();
            } else {
                calibration = false;
                GpsBus.getInstance().post(currentlocation);
                GpsBus.getInstance().post(calibration);
                this.onDestroy();
            }

            return;
        }

        // on n'utilise le point que si la précision est suffisante par rapport au paramétrage ; filtrage précision gps
        if (runningStarted && (currentlocation.getAccuracy() <= ParamHelper.accuracy)) {

            if (firstLocation) {
                previousLocation = currentlocation;
                firstLocation = false;
            }

            // si SIMULATION on ajoute de la dérive à latitude et longitude
            if (ParamHelper.simulation != 0) {

                double longitude = currentlocation.getLongitude();
                double latitude = currentlocation.getLatitude();
                int rand = RunningHelper.randInt();
                double random = (double) rand / 10000;
                currentlocation.setLongitude(random + longitude);

                rand = RunningHelper.randInt();
                random = (double) rand / 10000;
                currentlocation.setLatitude(random + latitude);
            }


            // si pas la première localisation => on peut calculer une vitesse
            float timeDelay = (currentlocation.getTime() - previousLocation.getTime()) / 1000;
            float vitesse = 0;

            // si temps écoulé, cad dire on est au secod point de prise
            if (timeDelay != 0) {
                vitesse = currentlocation.distanceTo(previousLocation) / timeDelay;

                // converti en km/h
                vitesse = (float) (vitesse * 3.6);
                // converti dans l'unité finale
                vitesse = RunningHelper.convertVelocityToUnity(vitesse, ParamHelper.velocitydUnity);
                currentlocation.setSpeed(vitesse);
            }

            // si la vitesse détectée est inférieure à la vitesse max autorisée dans l'unité désirée (filtrage vitesse)
            if (vitesse <= ParamHelper.vitesseMax && vitesse > 0) {

                // nbr de points pris
                nbrPointLocation++;

                // distance entre deux points consécutifs + distance totale (convertis dans unité finale)
                float distance = RunningHelper.convertLengthToUnity(currentlocation.distanceTo(previousLocation), ParamHelper.distanceUnity);
                distanceTotale = distanceTotale + distance;

                // addition des vitesses (déjà converties dans unité finale)
                velocityTotale = velocityTotale + vitesse;

                // appel de la tasksde calcul elevation + sauvegarde
                RunningStepBean runningStepBean = new RunningStepBean();
                runningStepBean.setLatitude(currentlocation.getLatitude());
                runningStepBean.setLongitude(currentlocation.getLongitude());
                runningStepBean.setAccuracy(currentlocation.getAccuracy());
                runningStepBean.setDistance(distance);
                runningStepBean.setVitesse(vitesse);
                runningStepBean.setTimeDelay(timeDelay);
                runningStepBean.setHorodatage(currentlocation.getTime());
                runningStepBean.setIdRunning(RunningActivity.idRunning);

                runningStepBean.setElevationUnity(ParamHelper.elevationUnity);

                SaveRunnigStepTask SaveRunnigStepTask = new SaveRunnigStepTask(getApplicationContext());
                SaveRunnigStepTask.execute(runningStepBean);

                // écriture sur GpsBus pour mise à jour Maps dans FragmentMaps
                // Location nécessaire car appel ensuite dans FragmentMaps de onLocationChanged(Location)
                GpsBus.getInstance().post(currentlocation);
            }

            // on sauve la location actuelle
            previousLocation = currentlocation;
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        Log.i(LOGSERVICE, "onDestroy GPSService ");

        // arrêt de la localisation
        stopLocationUpdate();

        // déconnexion du service google
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        // libération du lock CPU
        if (WakeLockHelper.cpuWakeLock != null && WakeLockHelper.cpuWakeLock .isHeld()) {
            WakeLockHelper.cpuWakeLock.release();
        }

        Toast.makeText(getApplicationContext(), "Service GPSService Arrêté", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOGSERVICE, "onConnectionFailed ");
    }

}


