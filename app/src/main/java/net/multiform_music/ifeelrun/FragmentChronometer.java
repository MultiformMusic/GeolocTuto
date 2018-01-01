package net.multiform_music.ifeelrun;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by michel.dio on 10/05/2017.
 *
 */

public class FragmentChronometer extends Fragment {

    // Vues du fragment
    private ImageView imgSeconds;
    private ImageView imgMinutes;
    private ImageView imgHours;
    private TextView chrono;
    /*private TextView chronoTime;
    private TextView elapsedTime;
    private TextView derive;*/

    private ImageButton buttonStart;
    private ImageButton buttonStop;
    private ImageButton buttonPause;

    private TextView velocityValue;
    private TextView distanceValue;
    private TextView elevationValue;
    private TextView velocityUnity;
    private TextView distanceUnity;
    private TextView elevationUnity;

    public static FragmentChronometer newInstance() {
        return new FragmentChronometer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chronometer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgSeconds = (ImageView) view.findViewById(R.id.imgsecond);
        imgMinutes = (ImageView) view.findViewById(R.id.imgminute);
        imgHours = (ImageView) view.findViewById(R.id.imghour);
        chrono = (TextView) view.findViewById(R.id.simpleChronometer);

        buttonStart = (ImageButton) view.findViewById(R.id.buttonStart);
        buttonStop = (ImageButton) view.findViewById(R.id.buttonStop);
        buttonPause = (ImageButton) view.findViewById(R.id.buttonPause);

        velocityValue = (TextView) view.findViewById(R.id.textVelocityValue);
        distanceValue = (TextView) view.findViewById(R.id.textDistanceValue);
        elevationValue = (TextView) view.findViewById(R.id.textElevationValue);

        velocityUnity = (TextView) view.findViewById(R.id.textVelocityUnity);
        distanceUnity = (TextView) view.findViewById(R.id.textDistanceUnity);

        elevationUnity = (TextView) view.findViewById(R.id.textElevationUnity);

        /*chronoTime = (TextView) view.findViewById(R.id.chronoTime);
        elapsedTime = (TextView) view.findViewById(R.id.elapsedTime);
        derive = (TextView) view.findViewById(R.id.derive);*/
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public ImageView getImgSeconds() {
        return imgSeconds;
    }

    public ImageView getImgMinutes() {
        return imgMinutes;
    }

    public ImageView getImgHours() {
        return imgHours;
    }

    public TextView getChrono() {
        return chrono;
    }


   /* public TextView getChronoTime() {
        return chronoTime;
    }

    public TextView getElapsedTime() {
        return elapsedTime;
    }

    public TextView getDerive() {
        return derive;
    }*/

    public ImageButton getButtonStop() {
        return buttonStop;
    }

    public ImageButton getButtonStart() {
        return buttonStart;
    }

    public ImageButton getButtonPause() {
        return buttonPause;
    }


    public TextView getVelocityValue() {
        return velocityValue;
    }

    public TextView getDistanceValue() {
        return distanceValue;
    }

    public TextView getElevationValue() {
        return elevationValue;
    }

    public TextView getVelocityUnity() {
        return velocityUnity;
    }

    public TextView getDistanceUnity() {
        return distanceUnity;
    }

    public TextView getElevationUnity() {
        return elevationUnity;
    }
}



