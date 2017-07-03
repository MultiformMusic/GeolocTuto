package net.multiform_music.geoloctuto.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.multiform_music.geoloctuto.bean.RunningStepBean;
import net.multiform_music.geoloctuto.bus.GpsBus;
import net.multiform_music.geoloctuto.helper.DatabaseHelper;
import net.multiform_music.geoloctuto.helper.ElevationHelper;
import net.multiform_music.geoloctuto.helper.RunningHelper;

/**
 * Created by michel.dio on 02/05/2017.
 *
 * Tache permettant de sauvegarder une ocalisation de la course (step)
 *
 * Appel au service d'elevation pour retrouver l'altitude de la location
 *
 */

public class SaveRunnigStepTask extends AsyncTask<RunningStepBean, Void, RunningStepBean> {


    private static final String LOGSERVICE = "### SaveRunnigStepTask";
    private Context context;

    public SaveRunnigStepTask (Context context){
        this.context = context;
    }

    @Override
    /**
     *  Processus
     *
     *  => appel API d'elevation
     *
     */
    protected RunningStepBean doInBackground(RunningStepBean... runningStepBean) {

        Log.i(LOGSERVICE, "doInBackground latitude = " + runningStepBean[0].getLatitude() + " -- longitude = " + runningStepBean[0].getLongitude());
        RunningStepBean runningStep = runningStepBean[0];

        double elevation = ElevationHelper.getElevationFromLocation(runningStepBean[0]);
        runningStep.setAltitude(RunningHelper.convertLengthToUnity((float)elevation, runningStep.getElevationUnity()));

        return runningStep;
    }

    /**
     *
     * Fin processus
     *
     * => sauvegarde running step en base
     *
     * @param runningStepBean step
     *
     */
    protected void onPostExecute(RunningStepBean runningStepBean) {
        Log.i(LOGSERVICE, "onPostExecute");

        // on poste sur le GPSBus pour mise à jour IHM RunnigActivity (vitesse, distance, altitude)
        GpsBus.getInstance().post(runningStepBean);

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.addRunningStepBean(runningStepBean);
    }
}
