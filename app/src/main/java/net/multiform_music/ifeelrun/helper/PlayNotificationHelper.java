package net.multiform_music.ifeelrun.helper;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Michel on 20/05/2017.
 *
 */

public class PlayNotificationHelper {

    // pamamétre de synthèse vocale + ausio
    private static AudioManager audioManager;
    private static TextToSpeech textToSpeech;

    private static AudioManager.OnAudioFocusChangeListener afChangeListener;

    /**
     *
     * Initialisation de player de texte
     *
     *
     * @param contextAppli context
     */
    public static void initplayNotification(Context contextAppli) {

        if (textToSpeech != null) {
            return;
        }

        audioManager = (AudioManager) contextAppli.getSystemService(Context.AUDIO_SERVICE);

        // NOTIFICATION
        ParamHelper.volumeAudio = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        textToSpeech = new TextToSpeech(contextAppli.getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {

                    if (ParamHelper.language.startsWith("fr")) {
                        textToSpeech.setLanguage(Locale.FRENCH);
                    } else {
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }


                } else {
                    System.out.println("**** ERRRORRORR ");
                }
            }
        });


        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ParamHelper.notificationVolume, 0);
            }

            @Override
            public void onError(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ParamHelper.volumeAudio, 0);
                audioManager.abandonAudioFocus(null);
            }
        });


    }

    /**
     *
     * Permet de parler un texte
     *
     * @param contex context
     * @param text text à jouer
     * @param test test
     */
    public static void playNotification(Context contex, String text, boolean reductAudioMusic, boolean test) {

        initplayNotification(contex);

        //AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder();

        // si pas test en cours on récup le volume audio courant
        if (!test) {
            ParamHelper.volumeAudio = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }

        // si réduction music
        if (reductAudioMusic) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ParamHelper.volumeAudio / 3, 0);
        }

        HashMap<String, String> params = new HashMap<>();
        // NOTIFICATION
        params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(audioManager.STREAM_MUSIC));
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");

        int result = audioManager.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request focus with low level.
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            // NOTIFICATION
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
        }
    }


}
