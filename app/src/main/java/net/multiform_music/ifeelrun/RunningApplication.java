package net.multiform_music.ifeelrun;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.facebook.stetho.Stetho;

import net.multiform_music.ifeelrun.helper.ParamHelper;
import net.multiform_music.ifeelrun.helper.WakeLockHelper;

import java.util.Locale;

/**
 * Created by michel.dio on 13/04/2017.
 *
 */



public class RunningApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        // récupération de la langue
        ParamHelper.language = Locale.getDefault().getLanguage();

        // récupération du paramétrage
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String unitVelocity = settings.getString("unitVelocity","km/h");
        String unitDistance = settings.getString("unitDistance","m");
        String unitElevation = settings.getString("unitElevation","m");
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
        ParamHelper.mapsType = mapsType;

    }

    @Override
    public void onTerminate() {

        if (WakeLockHelper.cpuWakeLock != null && WakeLockHelper.cpuWakeLock.isHeld()) {
            WakeLockHelper.cpuWakeLock.release();
            Log.i("ClockActitivy", "cpuWakeLock release");
        }
    }
}

