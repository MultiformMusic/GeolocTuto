package net.multiform_music.geoloctuto;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import net.multiform_music.geoloctuto.helper.ParamHelper;
import net.multiform_music.geoloctuto.helper.PlayNotificationHelper;
import net.multiform_music.geoloctuto.helper.RunningHelper;


/**
 * Created by Michel on 19/05/2017.
 *
 */

public class FragmentParameter extends Fragment {

    // gestion de la seekbar pour audio
    AudioManager audioManager;
    SeekBar notificationSeekBar;

    // checkbox
    public CheckBox checkNotificationDuringRunning;
    public CheckBox checkNotificationEndRunning;
    public CheckBox checkSimulation;

    // editText
    EditText editRunningName;

    private ChangeTypeActivityListener changeTypeActivityListener;

    public static FragmentParameter newInstance() {
        return new FragmentParameter();
    }

    public interface ChangeTypeActivityListener {

        void onChangeTypeActivity(String type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_parameter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /** récupération vues données */
        notificationSeekBar = (SeekBar)view.findViewById(R.id.seekBarNotification);
        notificationSeekBar.getProgressDrawable().setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
        checkSimulation = (CheckBox) view.findViewById(R.id.checkSimulation);
        checkNotificationDuringRunning = (CheckBox) view.findViewById(R.id.checkNotificationDuringRunning);
        checkNotificationDuringRunning.setOnCheckedChangeListener(onCheckedChangeListenerDuringRunning);
        checkNotificationEndRunning = (CheckBox) view.findViewById(R.id.checkNotificationEndRunning);
        checkNotificationEndRunning.setOnCheckedChangeListener(onCheckedChangeListenerEndRunning);
        Spinner spinnerActivity = (Spinner) view.findViewById(R.id.spinnerActivity);
        spinnerActivity.setOnItemSelectedListener(onItemSelectedListenerActivity);
        Spinner spinnerDelay = (Spinner) view.findViewById(R.id.spinnerDelay);
        spinnerDelay.setOnItemSelectedListener(onItemSelectedListenerDelay);
        editRunningName = (EditText) view.findViewById(R.id.editRunningName);

        // paramètre audio pour notification
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        notificationSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        notificationSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        // listener sur changement volume notification
        notificationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int volume, boolean b) {

                ParamHelper.notificationVolume = volume;
                PlayNotificationHelper.playNotification(getContext(), getActivity().getString(R.string.settings_notification_test), false, true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        changeTypeActivityListener = (ChangeTypeActivityListener) getActivity();
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListenerDuringRunning = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            ParamHelper.notificationDuringRunning = compoundButton.isChecked();
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListenerEndRunning = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            ParamHelper.notificationEndRunning = compoundButton.isChecked();
        }
    };

    private AdapterView.OnItemSelectedListener onItemSelectedListenerActivity = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            String activitySelected = adapterView.getSelectedItem().toString();
            ((RunningActivity) getActivity()).setActivityValue(activitySelected);

            if (activitySelected.equalsIgnoreCase("Course") || activitySelected.equalsIgnoreCase("Running")) {
                ParamHelper.vitesseMax = RunningHelper.convertVelocityToUnity(25, ParamHelper.velocitydUnity);
                ((RunningActivity) getActivity()).setActivityButtonStart("running");
                changeTypeActivityListener.onChangeTypeActivity("Running");
            } else if (activitySelected.equalsIgnoreCase("Marche") || activitySelected.equalsIgnoreCase("Walking")) {
                ParamHelper.vitesseMax = RunningHelper.convertVelocityToUnity(12, ParamHelper.velocitydUnity);
                changeTypeActivityListener.onChangeTypeActivity("Walking");
                ((RunningActivity) getActivity()).setActivityButtonStart("walking");
            } else if (activitySelected.equalsIgnoreCase("Vélo") || activitySelected.equalsIgnoreCase("Cycling")) {
                ParamHelper.vitesseMax = RunningHelper.convertVelocityToUnity(80, ParamHelper.velocitydUnity);
                changeTypeActivityListener.onChangeTypeActivity("Cycling");
                ((RunningActivity) getActivity()).setActivityButtonStart("cycling");
            } else if (activitySelected.equalsIgnoreCase("Test") || activitySelected.equalsIgnoreCase("Test")) {
                ParamHelper.vitesseMax = RunningHelper.convertVelocityToUnity(500, ParamHelper.velocitydUnity);
                changeTypeActivityListener.onChangeTypeActivity("Running");
                ((RunningActivity) getActivity()).setActivityButtonStart("running");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private AdapterView.OnItemSelectedListener onItemSelectedListenerDelay = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

            String delay =  adapterView.getSelectedItem().toString();
            delay = delay.substring(0, delay.indexOf(" "));
            ParamHelper.notificationDelay = Integer.valueOf(delay);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
}
