package net.multiform_music.ifeelrun.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import net.multiform_music.ifeelrun.bean.ChronometerBean;
import net.multiform_music.ifeelrun.bean.ChronometerBeanSavedState;
import net.multiform_music.ifeelrun.bean.NotificationBean;
import net.multiform_music.ifeelrun.helper.ParamHelper;
import net.multiform_music.ifeelrun.helper.PlayNotificationHelper;
import net.multiform_music.ifeelrun.helper.RunningHelper;

/**
 * Created by Michel on 09/05/2017.
 *
 */

public class ChronometerService extends Service {

    private static final String LOGSERVICE = "#### ChronometerService";
    public static final String INTENT_RESULT = "multiform_music.net.geoloctuto.chronometer.result";
    public static final String INTENT_RESULT_VALUES = "multiform_music.net.geoloctuto.chronometer.values";

    Thread myThread = null;

    public static int hours = 0;
    public static int minutes = 0;
    public static int seconds = 0;

    LocalBroadcastManager broadcaster;

    public static long startTimeMillis;
    private long pauseTimeMillis;

    // sauvegarde état du chronométre
    ChronometerBeanSavedState chronometerBeanSavedState;

    // flag pour savoir si chronomètre démarré
    public static boolean chronoStarted;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        Log.i(LOGSERVICE, "onCreate");

        CountDownRunner countDownRunner = new ChronometerService.CountDownRunner();
        countDownRunner.setContext(this);
        myThread = new Thread(countDownRunner);

        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(LOGSERVICE, "onStartCommand");

        // récupération de l'état du chrono
        chronometerBeanSavedState = intent.getParcelableExtra(RunningHelper.INTENT_PARAM_CHRONOMETER_SERVICE);

        // si l'état n'existe pas
        if (chronometerBeanSavedState == null) {

            startTimeMillis = System.currentTimeMillis();
            seconds = 0;
            minutes = 0;
            hours = 0;

            // si l'état existe c'est que click sur pause et redémarrage chrono)
        } else {

            startTimeMillis = chronometerBeanSavedState.getStartTimeMillis();
            pauseTimeMillis = chronometerBeanSavedState.getPauseTime();
            seconds = chronometerBeanSavedState.getSeconds();
            minutes = chronometerBeanSavedState.getMinutes();
            hours = chronometerBeanSavedState.getHours();
        }

        myThread.start();
        chronoStarted = true;

        Toast.makeText(getApplicationContext(), "Service ChronometerService Démarré", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(LOGSERVICE, "onDestroy Service ");

        myThread.interrupt();
        chronoStarted = false;

        Toast.makeText(getApplicationContext(), "Service ChronometerService Arrêté", Toast.LENGTH_SHORT).show();
    }

    class CountDownRunner implements Runnable {

        private Context context;
        // nombre de minutes écoulées (sans remise à zéro)
        int countMinutes = 0 ;
        int countSeconds = 0;

        public void setContext(Context context) {
            this.context = context;
        }

        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {

                    long currentTimeMillis = System.currentTimeMillis();

                    // temps passé en secondes
                    double elapsedTime = (currentTimeMillis - startTimeMillis) / 1000;

                    // temps chrono en secondes
                    double chronoTime = seconds + 60 * minutes + 3600 * hours;

                    // dérive du temsp crhono par rapport au temps écoulé (juste)
                    double derive = (chronoTime + pauseTimeMillis/1000) - elapsedTime;

                    Log.i(LOGSERVICE, "elapsedTime = " + elapsedTime + "-- chronoTime = " + chronoTime + " -- derive = " + derive);

                    // si dérive supérieure à 1s => on corrige
                    if (Math.abs(derive) >= 1) {

                        // temps corrigé passé en secondes par rapport à la dérive
                        int correctTime = (int) (elapsedTime - (pauseTimeMillis/1000));

                        seconds = correctTime % 60;
                        int totalMinutes = correctTime / 60;
                        minutes = totalMinutes % 60;
                        hours = totalMinutes / 60;
                    }

                    if (seconds == 60) {
                        seconds = 0;
                        minutes++;
                        countMinutes++;
                    }

                    if (minutes == 60) {
                        minutes = 0;
                        hours++;
                    }

                    if(hours == 12) {
                        hours = 0;
                    }

                    ChronometerBean chronometerBean = new ChronometerBean(hours, minutes, seconds, (int) elapsedTime, (int)chronoTime, (int)derive);
                    Intent intent = new Intent(INTENT_RESULT);
                    intent.putExtra(INTENT_RESULT_VALUES, chronometerBean);
                    broadcaster.sendBroadcast(intent);

                    // notification pendant le running
                    if (ParamHelper.notificationDuringRunning) {
                        // toutes les ...
                        if ((countMinutes != 0) && (countMinutes % ParamHelper.notificationDelay == 0)) {

                            String texteNotification = RunningHelper.getNotificationText(context, getNotificationBean(chronometerBean));
                            PlayNotificationHelper.playNotification(context, texteNotification, false, false);
                            countMinutes = 0;
                        }
                    }

                    countSeconds ++;
                    seconds++;

                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    Log.e("log_tag", "InterruptedException " + e.toString());
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    Log.e("log_tag", "Error is " + e.toString());
                }
            }
        }
    }

    /**
     *
     * Récupére les données de notification
     *
     * @param chronometerBean bean
     * @return notificationBean
     */
    private NotificationBean getNotificationBean(ChronometerBean chronometerBean) {

        NotificationBean notificationBean = new NotificationBean();

        float vitesseMoyenne = GPSService.velocityTotale / GPSService.nbrPointLocation;
        String vitesse = RunningHelper.roundDoubleTwoDigits((double) vitesseMoyenne);
        notificationBean.setVitesse(vitesse);

        notificationBean.setDistance(RunningHelper.roundDoubleTwoDigits((double)GPSService.distanceTotale));

        if(chronometerBean.getHours() != 0) {
            notificationBean.setHeure(Integer.toString(chronometerBean.getHours()));
        }
        if (chronometerBean.getMinutes() != 0) {
            notificationBean.setMinute(Integer.toString(chronometerBean.getMinutes()));
        }
        if (chronometerBean.getSeconds() != 0) {
            notificationBean.setSeconde(Integer.toString(chronometerBean.getSeconds()));
        }

        return notificationBean;
    }
}
