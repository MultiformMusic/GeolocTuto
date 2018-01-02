package net.multiform_music.ifeelrun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import net.multiform_music.ifeelrun.bean.ChronometerBean;
import net.multiform_music.ifeelrun.bean.ChronometerBeanSavedState;
import net.multiform_music.ifeelrun.bean.NotificationBean;
import net.multiform_music.ifeelrun.bean.RunningBean;
import net.multiform_music.ifeelrun.bean.RunningStepBean;
import net.multiform_music.ifeelrun.bus.GpsBus;
import net.multiform_music.ifeelrun.helper.DatabaseHelper;
import net.multiform_music.ifeelrun.helper.ParamHelper;
import net.multiform_music.ifeelrun.helper.PlayNotificationHelper;
import net.multiform_music.ifeelrun.helper.RunningHelper;
import net.multiform_music.ifeelrun.helper.WakeLockHelper;
import net.multiform_music.ifeelrun.service.ChronometerService;
import net.multiform_music.ifeelrun.service.GPSService;

import java.io.File;
import java.io.FileOutputStream;


/**
 * Created by michel.dio on 09/05/2017.
 *
 */

public class RunningActivity extends AppCompatActivity implements FragmentParameter.ChangeTypeActivityListener {

    private static final String LOGSERVICE = "##### RUNNING";

    // permet la réception réception de l'intent envoyé par ChronoService
    BroadcastReceiver receiver;

    // fragement du chronometre
    FragmentChronometer fragmentChronometer;
    // fragement du paramétrage
    FragmentParameter fragmentParameter;
    // fragement de la carte
    FragmentMaps fragmentMaps;

    // savoir si on a click sur bouton pause
    private boolean pause;
    private long pauseDebut;
    private long pauseTime;

    // paramètres de run
    ChronometerBeanSavedState chronometerBeanSavedState = new ChronometerBeanSavedState();
    public static boolean runningStarted;
    public static int idRunning;

    // view du commentaire
    EditText editTextComment;

    // template phrase notification
    String notificationTemplate;

    private String activityValue;
    private String activityButtonStart;

    public void setActivityValue(String activityValue) {
        this.activityValue = activityValue;
    }


    public String getActivityButtonStart() {
        return activityButtonStart;
    }

    public void setActivityButtonStart(String activityButtonStart) {
        this.activityButtonStart = activityButtonStart;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        // on récup la template de notification
        notificationTemplate = getResources().getString(R.string.notification_template);

        // fragment de chronometre
        fragmentChronometer = FragmentChronometer.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.chronometer_container, fragmentChronometer, "chronometerFragment")
                .commit();

        // fragment de paramétrage
        fragmentParameter = FragmentParameter.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.parameter_container, fragmentParameter, "parameterFragment")
                .commit();

        // fragment de la carte
        fragmentMaps = FragmentMaps.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, fragmentMaps, "mapFragment")
                .commit();

        // positionnement de l'id du running
        initIdRunning();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                final ChronometerBean chronometerBean = intent.getParcelableExtra(ChronometerService.INTENT_RESULT_VALUES);

                // animation secondes
                int seconds = chronometerBean.getSeconds();

                RotateAnimation rotateAnimationSeconds = new RotateAnimation(
                        seconds * 6, (seconds + 1) * 6,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);

                rotateAnimationSeconds.setInterpolator(new LinearInterpolator());
                rotateAnimationSeconds.setDuration(1000);
                rotateAnimationSeconds.setFillAfter(true);

                fragmentChronometer.getImgSeconds().startAnimation(rotateAnimationSeconds);

                // animation minutes
                int minutes = chronometerBean.getMinutes();

                RotateAnimation rotateAnimationMinutes = new RotateAnimation(
                        minutes * 6, (minutes + 1) * 6,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);

                rotateAnimationMinutes.setInterpolator(new LinearInterpolator());
                rotateAnimationMinutes.setDuration(60000);
                rotateAnimationMinutes.setFillAfter(true);

                fragmentChronometer.getImgMinutes().startAnimation(rotateAnimationMinutes);

                // animation heures
                int hours = chronometerBean.getHours();

                RotateAnimation rotateAnimationHours = new RotateAnimation(
                        hours * 30, (hours + 1) * 30,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);

                rotateAnimationHours.setInterpolator(new LinearInterpolator());
                rotateAnimationHours.setDuration(3600000);
                rotateAnimationHours.setFillAfter(true);

                fragmentChronometer.getImgHours().startAnimation(rotateAnimationHours);

                // horloge numérique
                String hoursChrono = ((Integer.valueOf(hours).toString()).length() < 2) ? ("0" + Integer.valueOf(hours).toString()) : Integer.valueOf(hours).toString();
                String minutesChrono = (Integer.valueOf(minutes).toString()).length() < 2 ? "0" + Integer.valueOf(minutes).toString() : Integer.valueOf(minutes).toString();
                String secondsChrono = (Integer.valueOf(seconds).toString()).length() < 2 ? "0" + Integer.valueOf(seconds).toString() : Integer.valueOf(seconds).toString();
                String text = hoursChrono + ":" + minutesChrono + ":" + secondsChrono;
                fragmentChronometer.getChrono().setText(text);

                /*fragmentChronometer.getElapsedTime().setText(Integer.valueOf(chronometerBean.getElapsedTime()).toString());
                fragmentChronometer.getChronoTime().setText(Integer.valueOf(chronometerBean.getChronoTime()).toString());
                fragmentChronometer.getDerive().setText(Integer.valueOf(chronometerBean.getDerive()).toString());
                */
            }
        };

    }

    /**
     *
     * Démarrage du service {@link ChronometerService}
     *
     */
    public View.OnClickListener clickListenerStart = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            runningStarted = true;

            RunningActivity runningActivity = (RunningActivity)v.getContext();
            runningActivity.onChangeTypeActivity(runningActivity.getActivityButtonStart());

            fragmentChronometer.getButtonPause().setImageResource(R.drawable.icon_pause_run);
            fragmentChronometer.getButtonPause().setClickable(true);

            fragmentChronometer.getButtonStop().setImageResource(R.drawable.icon_stop_run);
            fragmentChronometer.getButtonStop().setEnabled(true);

            // démarrage du ChronometerService
            Intent intentChronometerService =  new Intent(v.getContext(), ChronometerService.class);
            startService(intentChronometerService);

            // démarrage du GPSService (passage de ParamBean)
            startGpsService();

            // message running start
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.running_toast_start), Toast.LENGTH_SHORT).show();

        }
    };

    /**
     *
     * Pause du service {@link ChronometerService}
     *
     */
    public View.OnClickListener clickListenerPause = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (pause) {

                fragmentChronometer.getButtonPause().setImageResource(R.drawable.icon_pause_run);
                pause = false;
                runningStarted = true;

                // calcul du temps de pause
                pauseTime = pauseTime + (System.currentTimeMillis() - pauseDebut);
                chronometerBeanSavedState.setPauseTime(pauseTime);

                // temps de pause positionné
                Intent intentChronometerService =  new Intent(v.getContext(), ChronometerService.class);
                intentChronometerService.putExtra(RunningHelper.INTENT_PARAM_CHRONOMETER_SERVICE, chronometerBeanSavedState);
                startService(intentChronometerService);

                // start GPSService
                startGpsService();

                // message redémarrage
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.running_toast_start), Toast.LENGTH_SHORT).show();


            } else {

                pause = true;
                runningStarted = false;

                // on sauve l'état du crhono avant la pause
                chronometerBeanSavedState = new ChronometerBeanSavedState(ChronometerService.hours, ChronometerService.minutes, ChronometerService.seconds, ChronometerService.startTimeMillis, 0);

                chronometerBeanSavedState.setHours(ChronometerService.hours);
                chronometerBeanSavedState.setMinutes(ChronometerService.minutes);
                chronometerBeanSavedState.setSeconds(ChronometerService.seconds);
                chronometerBeanSavedState.setStartTimeMillis(ChronometerService.startTimeMillis);

                // on change l'image
                fragmentChronometer.getButtonPause().setImageResource(R.drawable.icon_pause_run_clik);

                // on flag le temps pause début
                pauseDebut = System.currentTimeMillis();

                // stop ChronometerService
                stopService(new Intent(v.getContext(), ChronometerService.class));

                // stop GPSService
                stopGpsService();

                // message de mise en pause
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.running_toast_pause), Toast.LENGTH_SHORT).show();

            }
        }
    };

    /**
     *
     * Arrêt du service {@link ChronometerService}
     *
     */
    public View.OnClickListener clickListenerStop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // le running est fini
            runningStarted = false;

            RunningActivity runningActivity = (RunningActivity)v.getContext();
            runningActivity.onChangeTypeActivity(runningActivity.getActivityButtonStart());

            fragmentChronometer.getButtonPause().setImageResource(R.drawable.icon_pause_run_clik);
            fragmentChronometer.getButtonStop().setImageResource(R.drawable.icon_stop_run_click);
            //fragmentChronometer.getButtonStart().setClickable(true);
            fragmentChronometer.getButtonPause().setClickable(false);
            fragmentChronometer.getButtonStop().setEnabled(false);

            // arrêt des services
            stopService(new Intent(v.getContext(), ChronometerService.class));
            stopGpsService();

            // création de la course associée si au moint une location existe
            if (GPSService.nbrPointLocation > 0) {

                RunningBean runningBean = new RunningBean();
                runningBean.setId(idRunning);
                String runningName = fragmentParameter.editRunningName.getText().toString();
                if (runningName.trim().length() == 0) {
                    runningBean.setPlaceName(getString(R.string.settings_running_default));
                } else {
                    runningBean.setPlaceName(runningName);
                }
                runningBean.setDate(RunningHelper.getDateDuJour());
                runningBean.setDistance(RunningHelper.roundDouble(GPSService.distanceTotale));
                runningBean.setVelocity(RunningHelper.roundDouble(GPSService.velocityTotale / (GPSService.nbrPointLocation - 1)));
                runningBean.setNbrPoints(GPSService.nbrPointLocation);
                runningBean.setUnitVelocity(ParamHelper.velocitydUnity);
                runningBean.setUnitDistance(ParamHelper.distanceUnity);
                runningBean.setUnitElevation(ParamHelper.elevationUnity);
                runningBean.setTemps(fragmentChronometer.getChrono().getText().toString());

                // demande génération de la carte en bitmap
                //fragmentMaps.getBitmapFromMap();

                DatabaseHelper databaseHelper = new DatabaseHelper(v.getContext());

                // calcul du denivele
                double maxAltitude = databaseHelper.getMaxAltitude(idRunning);
                double minAltitude = databaseHelper.getMinAltitude(idRunning);
                runningBean.setDenivele(RunningHelper.roundDouble(maxAltitude - minAltitude));

                runningBean.setActivity(activityValue);
                LatLng latLng = databaseHelper.getFirstLocation(idRunning);
                if (latLng != null) {
                    runningBean.setCity(fragmentMaps.getCityName(databaseHelper.getFirstLocation(idRunning)));
                } else {
                    runningBean.setCity("");
                }

                // on notifie
                if (ParamHelper.notificationEndRunning) {

                    String texteNotification = RunningHelper.getNotificationText(v.getContext(), getNotificationBean(runningBean));
                    PlayNotificationHelper.playNotification(v.getContext(), texteNotification, false, false);
                }

                // création course en base
                boolean ok = databaseHelper.addRunningBean(runningBean);

                // message de sauvegarde OK ou ERROR
                if (ok) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.running_toast_save), Toast.LENGTH_LONG).show();
                } else {
                    RunningHelper.afficheToastError((RunningActivity) v.getContext(), getResources().getString(R.string.running_toast_save_error));
                }

                // ajout d'un commentaire
                addComment(v, databaseHelper, runningBean);

                // dessin du parcours sur la map
                fragmentMaps.writeMapsPath(idRunning);

                // initialisation nouveau id running
                initIdRunning();

            }
        }
    };


    /**
     * Ajout d'un commentaire
     *
     * @param v view
     * @param databaseHelper helper
     * @param runningBean bean
     */


    private void addComment(final View v, final DatabaseHelper databaseHelper, final RunningBean runningBean) {

        LayoutInflater li = LayoutInflater.from(v.getContext());
        View commentddDialogView = li.inflate(R.layout.dialog_comment, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext(), R.style.dialogTheme);
        alertDialogBuilder.setTitle(v.getContext().getString(R.string.dialog_comment_title));
        alertDialogBuilder.setIcon(R.drawable.icon_add);
        alertDialogBuilder.setView(commentddDialogView);

        editTextComment = (EditText) commentddDialogView.findViewById(R.id.commentDialogInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_comment_add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // sauvegarde commentaire

                                boolean ok = databaseHelper.majRunningComment(runningBean.getId(), editTextComment.getText().toString());

                                if (ok) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_comment_add_ok), Toast.LENGTH_LONG).show();
                                } else {
                                    RunningHelper.afficheToastError((RunningActivity) v.getContext(), getResources().getString(R.string.dialog_comment_add_error));
                                }
                            }
                        })
                .setNegativeButton(R.string.dialog_comment_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(LOGSERVICE, "onResume");

        fragmentChronometer.getButtonStart().setOnClickListener(clickListenerStart);
        fragmentChronometer.getButtonStop().setOnClickListener(clickListenerStop);
        fragmentChronometer.getButtonPause().setOnClickListener(clickListenerPause);

        fragmentChronometer.getVelocityUnity().setText(ParamHelper.velocitydUnity);
        fragmentChronometer.getDistanceUnity().setText(ParamHelper.distanceUnity);
        fragmentChronometer.getElevationUnity().setText(ParamHelper.elevationUnity);

        // test si le service Chrono est démarré, si oui on modifie les icones et on redémarre le service GPS pour Maps
        // on désactive bouton start et active bouton pause et stop
        if (ChronometerService.chronoStarted) {

            //fragmentChronometer.getButtonStart().setImageResource(R.drawable.icon_start_run_click);
            //fragmentChronometer.getButtonStart().setClickable(false);
            onChangeTypeActivity(getActivityButtonStart());

            fragmentChronometer.getButtonPause().setImageResource(R.drawable.icon_pause_run);
            fragmentChronometer.getButtonPause().setClickable(true);

            fragmentChronometer.getButtonStop().setImageResource(R.drawable.icon_stop_run);
            fragmentChronometer.getButtonStop().setEnabled(true);

            // si le service Chrono est démarré alors on démarre aussi le service GPS
            startGpsService();
        }

        // si le bouton Start est cliquable => on désactive les boutons stop et pause
        else if (fragmentChronometer.getButtonStart().isClickable()) {

            fragmentChronometer.getButtonPause().setImageResource(R.drawable.icon_pause_run_clik);
            fragmentChronometer.getButtonPause().setClickable(false);

            fragmentChronometer.getButtonStop().setImageResource(R.drawable.icon_stop_run_click);
            fragmentChronometer.getButtonStop().setEnabled(false);

            // si le bouton Start n'est pas cliquable
        } else {

            // détermination de l'état bouton pause suivant en pause ou non
            if (pause) {
                fragmentChronometer.getButtonPause().setImageResource(R.drawable.icon_pause_run_clik);
                fragmentChronometer.getButtonPause().setClickable(true);
            } else {
                fragmentChronometer.getButtonPause().setImageResource(R.drawable.icon_pause_run);
                fragmentChronometer.getButtonPause().setClickable(true);
            }
        }

        if(WakeLockHelper.cpuWakeLock != null && WakeLockHelper.cpuWakeLock.isHeld()){
            WakeLockHelper.cpuWakeLock.release();
            Log.i("ClockActitivy", "cpuWakeLock release");
        }

        // init du paramètrage
        initParamHelper();

        // enregistrement sur le Bus GPS
        GpsBus.getInstance().register(this);

        // positionnement du type de maps
        fragmentMaps.setMapType(ParamHelper.mapsType);

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(LOGSERVICE, "onPause");

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        WakeLockHelper.cpuWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RunningWakeLock");

        if (WakeLockHelper.cpuWakeLock != null && !WakeLockHelper.cpuWakeLock.isHeld()) {
            WakeLockHelper.cpuWakeLock.acquire();
            Log.i("ClockActitivy", "cpuWakeLock acquire");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(LOGSERVICE, "onStart");

        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(ChronometerService.INTENT_RESULT));

        // on démarre le service GPS juste pour la première localisation (calibration), il va s'arrêter automatiquement lorsque la calibration
        // est terminée
        // on grise le bouton Start
        if (GPSService.calibration) {

            fragmentChronometer.getButtonStart().setImageResource(R.drawable.icon_start_run_click);
            fragmentChronometer.getButtonStart().setClickable(false);
            startGpsService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i(LOGSERVICE, "onStop");

        // receiver du ChronometerService
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        // Bus du GPSService
        GpsBus.getInstance().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(LOGSERVICE, "onDestroy");

        stopService(new Intent(this, ChronometerService.class));

        if (WakeLockHelper.cpuWakeLock != null && WakeLockHelper.cpuWakeLock.isHeld()) {
            WakeLockHelper.cpuWakeLock.release();
            Log.i("ClockActitivy", "cpuWakeLock release");
        }

        // Bus du GPSService
        GpsBus.getInstance().unregister(this);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        GPSService.calibration = true;
        stopGpsService();
        // Bus du GPSService
        GpsBus.getInstance().unregister(this);

        Intent intentHome = new Intent(this, HomeActivity.class);
        startActivity(intentHome);
    }

    /**
     *
     * Démarrage du {@link GPSService}
     *
     */
    private void startGpsService() {

        // simulation
        ParamHelper.simulation = fragmentParameter.checkSimulation.isChecked() ? 1 : 0;

        Intent intentGpsService =  new Intent(this, GPSService.class);
        startService(intentGpsService);
    }

    /**
     *
     * Arrêt du {@link GPSService}
     *
     */
    private void stopGpsService() {

        runningStarted = false;
        stopService(new Intent(this, GPSService.class));
    }

    /**
     *
     * Réception données altitude du bus {@link net.multiform_music.ifeelrun.task.SaveRunnigStepTask}
     *
     * @param runningStepBean runningStepBean
     *
     */
    public void onEvent(RunningStepBean runningStepBean){

        fragmentChronometer.getVelocityValue().setText(RunningHelper.roundDoubleTwoDigits((double) runningStepBean.getVitesse()) + " ");
        fragmentChronometer.getDistanceValue().setText(RunningHelper.roundDoubleTwoDigits((double) GPSService.distanceTotale) + " ");
        fragmentChronometer.getElevationValue().setText(RunningHelper.roundDoubleTwoDigits(runningStepBean.getAltitude()) + " ");
    }

    /**
     *
     * Quand calibration OK => event pour activer le bouton Start
     *
     * @param calibration calibration
     */
    public void onEvent(Boolean calibration){

        if (!calibration) {

            onChangeTypeActivity(getActivityButtonStart());
        }
    }

    /**
     *
     * Initialisation de l'idRunning : nécessaire pour l'enregistrement des RunningStepBean
     */
    private void initIdRunning() {

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        idRunning = (int) databaseHelper.getNextAutoIncrement("Running");
    }

    /**
     *
     * Initialisation du paramérage
     *
     */
    private void initParamHelper() {

        // récupération du paramétrage
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String unitVelocity = settings.getString("unitVelocity","km/h");
        String unitDistance = settings.getString("unitDistance","m");
        String unitElevation = settings.getString("unitElevation","m");
        String unitHeight = settings.getString("unitHeight","cm");
        String unitWeight = settings.getString("unitWeight","kg");
        String mapsType = settings.getString("mapsType","normal");

        // initialisation ParamHelper
        ParamHelper.simulation = 0;
        ParamHelper.accuracy = 50;
        ParamHelper.distanceMin = 0;
        ParamHelper.vitesseMax = 100000;
        ParamHelper.zoom = 17;
        ParamHelper.velocitydUnity = unitVelocity;
        ParamHelper.distanceUnity = unitDistance;
        ParamHelper.elevationUnity = unitElevation;
        ParamHelper.heightUnity = unitHeight;
        ParamHelper.weightUnity = unitWeight;
        ParamHelper.mapsType = mapsType;

        ParamHelper.notificationDuringRunning = fragmentParameter.checkNotificationDuringRunning.isChecked();
        ParamHelper.notificationEndRunning = fragmentParameter.checkNotificationEndRunning.isChecked();

        // on repositionne les valeurs d'unités
        fragmentChronometer.getVelocityUnity().setText(ParamHelper.velocitydUnity);
        fragmentChronometer.getDistanceUnity().setText(ParamHelper.distanceUnity);
        fragmentChronometer.getElevationUnity().setText(ParamHelper.elevationUnity);

    }

    /**
     *
     * Récupére les données de notification
     *
     * @param runningBean bean
     * @return notificationBean
     */
    private NotificationBean getNotificationBean(RunningBean runningBean) {

        NotificationBean notificationBean = new NotificationBean();

        notificationBean.setVitesse(RunningHelper.roundDoubleTwoDigits(runningBean.getVelocity()));
        notificationBean.setDistance(RunningHelper.roundDoubleTwoDigits(runningBean.getDistance()));

        String[] tempsSplit = runningBean.getTemps().split(":");
        String heure = tempsSplit[0];
        if (heure.equalsIgnoreCase("00")) {
            heure = null;
        } else if (heure.startsWith("0")) {
            heure = heure.substring(1);
        }
        notificationBean.setHeure(heure);


        String minute = tempsSplit[1];
        if (minute.equalsIgnoreCase("00")) {
            minute = null;
        } else if (minute.startsWith("0")) {
            minute = minute.substring(1);
        }
        notificationBean.setMinute(minute);

        String seconde = tempsSplit[2];
        if (seconde.equalsIgnoreCase("00")) {
            seconde = null;
        } else if (seconde.startsWith("0")) {
            seconde = seconde.substring(1);
        }
        notificationBean.setSeconde(seconde);

        return notificationBean;
    }

    /**
     *
     * Permet de changer l'icone start suivant l'activité sélectionnée
     *
     * @param typeActivity type
     */
    public void onChangeTypeActivity(String typeActivity) {

        if (typeActivity.equalsIgnoreCase("running") && !runningStarted) {

            fragmentChronometer.getButtonStart().setImageResource(R.drawable.icon_running_circle_size);
            fragmentChronometer.getButtonStart().setClickable(true);

        } else if (typeActivity.equalsIgnoreCase("running") && runningStarted) {

            fragmentChronometer.getButtonStart().setImageResource(R.drawable.icon_running_circle_size_click);
            fragmentChronometer.getButtonStart().setClickable(false);
        }

        if (typeActivity.equalsIgnoreCase("walking") && !runningStarted) {

            fragmentChronometer.getButtonStart().setImageResource(R.drawable.icon_start_walking_size);
            fragmentChronometer.getButtonStart().setClickable(true);

        } else if (typeActivity.equalsIgnoreCase("walking") && runningStarted) {

            fragmentChronometer.getButtonStart().setImageResource(R.drawable.icon_start_walking_size_click);
            fragmentChronometer.getButtonStart().setClickable(false);
        }

        if (typeActivity.equalsIgnoreCase("cycling") && !runningStarted) {

            fragmentChronometer.getButtonStart().setImageResource(R.drawable.icon_start_cycling_size);
            fragmentChronometer.getButtonStart().setClickable(true);

        } else if (typeActivity.equalsIgnoreCase("cycling") && runningStarted) {

            fragmentChronometer.getButtonStart().setImageResource(R.drawable.icon_start_cycling_size_click);
            fragmentChronometer.getButtonStart().setClickable(false);
        }
    }


    /**
     * traitement du bitmap : sauvegarde sur le filesystem local + mise à jour base avec chemin vers fichier
     *
     * @param bitmapMaps
     */
    public void onGenerationMapToBitmap(int idRunning, Bitmap bitmapMaps) {

        String picturePath = "";
        File internalStorage = this.getDir("RunningMapsPictures", Context.MODE_PRIVATE);
        File mapsFilePath = new File(internalStorage, "idRunning" + idRunning + ".png");
        picturePath = mapsFilePath.toString();

        FileOutputStream fos = null;

        try {

            fos = new FileOutputStream(picturePath);
            bitmapMaps.compress(Bitmap.CompressFormat.PNG, 50 /*quality*/, fos);
            fos.close();
        }
        catch (Exception ex) {
            Log.i("FILE", "Problem saving picture", ex);
            picturePath = "";
        }

        // sauvegarde du chemin de la mpa en base
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.majRunningMapFilePath(idRunning, picturePath);

    }
}


