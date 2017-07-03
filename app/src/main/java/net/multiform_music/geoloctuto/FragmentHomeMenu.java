package net.multiform_music.geoloctuto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * Created by Michel on 07/05/2017.
 *
 */

public class FragmentHomeMenu extends Fragment {

    // boutons
    private ImageButton buttonStatistics;
    private ImageButton buttonRunning;
    private ImageButton buttonParameter;

    // interface listener du click sur boutons
    HomeMenuClickListener homeMenuClickListener;

    public static FragmentHomeMenu newInstance() {
        return new FragmentHomeMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonRunning = (ImageButton) view.findViewById(R.id.button_new_run);
        buttonRunning.setOnClickListener(clickListenerRunning);

        buttonStatistics = (ImageButton) view.findViewById(R.id.button_statistics);
        buttonStatistics.setOnClickListener(clickListenerStatistic);

        buttonParameter = (ImageButton) view.findViewById(R.id.button_parameter);
        buttonParameter.setOnClickListener(clickListenerParameter);

        // ajout du listener gérant les boutons
        homeMenuClickListener = (HomeMenuClickListener) getActivity();
    }

    private View.OnClickListener clickListenerRunning = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            buttonRunning.setImageResource(R.drawable.icon_running_circle_size_click);
            homeMenuClickListener.onClickRunning();
        }
    };

    private View.OnClickListener clickListenerStatistic = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            buttonStatistics.setImageResource(R.drawable.icon_statistic_circle_size_click);
            homeMenuClickListener.onClickStatistic();
        }
    };

    private View.OnClickListener clickListenerParameter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            buttonParameter.setImageResource(R.drawable.icon_parameter_circle_size_click);
            homeMenuClickListener.onClickParameter();
        }
    };

    /**
     * Interface gérant les clicks sur bouton IHM
     */
    public interface HomeMenuClickListener {

        void onClickRunning();
        void onClickParameter();
        void onClickStatistic();
    }

    @Override
    public void onResume() {
        super.onResume();

        // on change image bouton cliqué : image initiale
        buttonRunning.setImageResource(R.drawable.icon_running_circle_size);
        buttonParameter.setImageResource(R.drawable.icon_parameter_circle_size);
        buttonStatistics.setImageResource(R.drawable.icon_statistic_circle_size);
    }

}
