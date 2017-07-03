package net.multiform_music.geoloctuto.helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.multiform_music.geoloctuto.R;
import net.multiform_music.geoloctuto.bean.NotificationBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Michel on 28/04/2017.
 *
 */

public final class RunningHelper {

    public static final String INTENT_PARAM_CHRONOMETER_SERVICE = "multiform_music.net.intent.paramChronometer";

    /**
     *
     * Converti une long time en date String
     *
     * @param time temps
     * @return string date dd/mm/yyyy HH:mm:ss
     */
    public static String convertDateLongToString(long time) {

        Date date=new Date(time);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

        return df2.format(date);
    }

    /**
     *
     * Random un int
     *
     * @return int
     */
    public static int randInt() {

        // Usually this can be a field rather than a method variable
        Random random = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return random.nextInt(100);
    }

    /**
     *
     * Arrondi un double à 2 digits
     *
     *
     * @param value valeur
     * @return double arrondi au 10ième
     */
    public static double roundDouble(double value) {
        value = Math.floor(value * 100) / 100;
        return value;
    }

    /**
     *
     * Date du jour en jj/MM/yyyy HH:mm:ss
     *
     * @return string date jour jj/MM/yyyy HH:mm:ss
     */
    public static String getDateDuJour() {

        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        return formatter.format(date);
    }

    /**
     *
     * Arrrondi un Double sur 2 digits en String
     *
     * @param value valeur
     * @return String du Double à 2 digits
     */
    public static String roundDoubleTwoDigits(Double value) {

        return String.format("%1.2f", value);
    }


    /**
     *
     * Arrondi un Double à 2 digits en Double
     *
     * @param value valeur
     * @return Double à 2 digits
     */
    public static Double roundDouble(Double value) {

        String str = String.format("%1.2f", value);
        return Double.valueOf(str);
    }

    /**
     *
     * EFFECTUER LES CONVERSIONS :
     *
     * km/h  (mph)   1 mph = 1,609 km/h
     * min/km
     * mètre
     * yard (0.914 m)
     *
     * @param velocityInit vitesse
     * @param unitVelocity unité
     * @return vitesse convertie
     */
    public static float convertVelocityToUnity(float velocityInit, String unitVelocity) {

        float velocityFinale = velocityInit;

        if (unitVelocity.equalsIgnoreCase("km/h")) {
            return velocityInit;
        } else if (unitVelocity.equalsIgnoreCase("mph")) {
            velocityFinale =  (float) (velocityInit * 1.609);
        } else if (unitVelocity.equalsIgnoreCase("min/km")) {
            if (velocityInit > 0) {
                velocityFinale = 60 / velocityInit;
            }
        } else if (unitVelocity.equalsIgnoreCase("min/mph")) {
            if (velocityInit > 0) {
                velocityFinale = (float) ((60 * 1.609) / velocityInit);
            }
        }

        return velocityFinale;
    }

    /**
     *
     * EFFECTUER LES CONVERSIONS :
     *
     * km/h  (mph)   1 mph = 1,609 km/h
     * min/km
     * mètre
     * yard (0.914 m)
     *
     * @param lengthInit init
     * @param unitLength unit
     * @return float result
     */
    public static float convertLengthToUnity(float lengthInit, String unitLength) {

        float lengthFinale = lengthInit;

        if (unitLength.equalsIgnoreCase("m")) {
            return lengthInit;
        } else if (unitLength.equalsIgnoreCase("km")) {
            lengthFinale = lengthInit / 1000;
        } else if (unitLength.equalsIgnoreCase("y")) {
            lengthFinale = (float) (lengthInit / 0.914);
        } else if (unitLength.equalsIgnoreCase("mille")) {
            lengthFinale = lengthInit / 1609;
        }

        return lengthFinale;
    }

    /**
     * Affiche un Toast d'erreur
     *
     * @param activity activité
     * @param texte texte
     */
    public static void afficheToastError(Activity activity, String texte) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View layoutErrorToast = inflater.inflate(R.layout.custom_toast_error, (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        Toast custToast = new Toast(activity);
        TextView textSaveToast = (TextView) layoutErrorToast.findViewById(R.id.toast_error_text);
        textSaveToast.setText(texte);
        custToast.setView(layoutErrorToast);
        custToast.setDuration(Toast.LENGTH_LONG);
        custToast.show();
    }

    /**
     *
     * Récupére le texte de notification
     *
     * @param context context
     * @param notificationBean bean
     * @return texte
     */
    public static String getNotificationText(Context context, NotificationBean notificationBean) {

        StringBuilder textResult = new StringBuilder();

        String velocityUnityTexte = "";
        String distanceUnityTexte = "";

        // détermination unité de vitesse
        switch (ParamHelper.velocitydUnity) {
            case "km/h":
                velocityUnityTexte = context.getResources().getString(R.string.notification_velocity_km_hour);
                break;
            case "min/km":
                velocityUnityTexte = context.getResources().getString(R.string.notification_velocity_min_km);
                break;
            case "mph":
                velocityUnityTexte = context.getResources().getString(R.string.notification_velocity_miles_hour);
                break;
            case "min/mph":
                velocityUnityTexte = context.getResources().getString(R.string.notification_velocity_min_mille);
                break;
        }

        // détermination unité de distance
        if (ParamHelper.distanceUnity.equalsIgnoreCase("m")) {
            distanceUnityTexte = context.getResources().getString(R.string.notification_distance_meter);
        } else if (ParamHelper.distanceUnity.equalsIgnoreCase("km")) {
            distanceUnityTexte = context.getResources().getString(R.string.notification_distance_kilometer);
        } else if (ParamHelper.distanceUnity.equalsIgnoreCase("y")) {
            distanceUnityTexte = context.getResources().getString(R.string.notification_distance_yard);
        } else if (ParamHelper.distanceUnity.equalsIgnoreCase("mile")) {
            distanceUnityTexte = context.getResources().getString(R.string.notification_distance_mile);
        }

        textResult.append(context.getResources().getString(R.string.notification_intro));
        textResult.append(" , ");

        // vitesse
        textResult.append(context.getResources().getString(R.string.notification_velocity));
        textResult.append(" ");
        textResult.append(notificationBean.getVitesse());
        textResult.append(" ");
        textResult.append(velocityUnityTexte);
        textResult.append(" , ");

        // distance
        textResult.append(context.getResources().getString(R.string.notification_distance));
        textResult.append(" ");
        textResult.append(notificationBean.getDistance());
        textResult.append(" ");
        textResult.append(distanceUnityTexte);
        textResult.append(" , ");

        // temps
        textResult.append(context.getResources().getString(R.string.notification_time));
        if (notificationBean.getHeure() != null) {
            textResult.append(" ");
            textResult.append(notificationBean.getHeure());
            textResult.append(" ");
            textResult.append(context.getResources().getString(R.string.notification_time_hour));
        }

        if (notificationBean.getMinute() != null) {
            textResult.append(" ");
            textResult.append(notificationBean.getMinute());
            textResult.append(" ");
            textResult.append(context.getResources().getString(R.string.notification_time_minute));
        }

        if (notificationBean.getSeconde() != null) {
            textResult.append(" ");
            textResult.append(notificationBean.getSeconde());
            textResult.append(" ");
            textResult.append(context.getResources().getString(R.string.notification_time_second));
        }

        return textResult.toString();

    }

}

