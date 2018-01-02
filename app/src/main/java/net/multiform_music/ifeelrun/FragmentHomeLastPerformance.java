package net.multiform_music.ifeelrun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.multiform_music.ifeelrun.bean.RunningBean;
import net.multiform_music.ifeelrun.helper.DatabaseHelper;

/**
 * Created by michel.dio on 01/06/2017.
 *
 *
 */

public class FragmentHomeLastPerformance extends Fragment {

    TextView title;
    TextView dateValue;
    TextView cityValue;
    TextView velocityValue;
    TextView velocityUnity;
    TextView distanceValue;
    TextView distanceUnity;
    TextView chrono;

    ImageView dateImage;
    ImageView chronoImage;
    ImageView velocityImage;
    ImageView distanceImage;

    public static FragmentHomeLastPerformance newInstance() {
        return new FragmentHomeLastPerformance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home_last_performance, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        int idLastRunning = databaseHelper.getLastIdRecordRunning();

        dateImage = (ImageView) view.findViewById(R.id.iconDate);
        chronoImage = (ImageView) view.findViewById(R.id.iconChrono);
        velocityImage = (ImageView) view.findViewById(R.id.iconSpeed);
        distanceImage = (ImageView) view.findViewById(R.id.iconDistance);

        // si pas de première course => on vide * message
        if (idLastRunning == 0) {

            title = (TextView) view.findViewById(R.id.title);
            title.setText(getResources().getString(R.string.home_last_performance_no_perf));

            dateImage.setVisibility(View.INVISIBLE);
            chronoImage.setVisibility(View.INVISIBLE);
            velocityImage.setVisibility(View.INVISIBLE);
            distanceImage.setVisibility(View.INVISIBLE);

        } else {

            RunningBean runningBean = databaseHelper.getRunningBean(idLastRunning);

            dateImage.setVisibility(View.VISIBLE);
            chronoImage.setVisibility(View.VISIBLE);
            velocityImage.setVisibility(View.VISIBLE);
            distanceImage.setVisibility(View.VISIBLE);

            dateValue = (TextView) view.findViewById(R.id.dateValue);
            dateValue.setText(runningBean.getDate());

            cityValue = (TextView) view.findViewById(R.id.cityValue);
            if (runningBean.getCity() != null && !runningBean.getCity().equalsIgnoreCase("")) {
                cityValue.setText("(" + runningBean.getCity() + ")");
            }

            velocityValue = (TextView) view.findViewById(R.id.velocityValue);
            velocityValue.setText(" " + Double.toString(runningBean.getVelocity()));
            velocityUnity = (TextView) view.findViewById(R.id.velocityUnity);
            velocityUnity.setText(" " + runningBean.getUnitVelocity());

            distanceValue = (TextView) view.findViewById(R.id.distanceValue);
            distanceValue.setText(" " + runningBean.getDistance());
            distanceUnity = (TextView) view.findViewById(R.id.distanceUnity);
            distanceUnity.setText(" " + runningBean.getUnitDistance());

            chrono = (TextView) view.findViewById(R.id.chronoValue);
            chrono.setText(" " + runningBean.getTemps());
        }
    }
}
