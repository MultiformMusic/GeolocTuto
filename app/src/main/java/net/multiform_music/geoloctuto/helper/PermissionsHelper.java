package net.multiform_music.geoloctuto.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by michel.dio on 04/05/2017.
 *
 */

public class PermissionsHelper {

    public static boolean locationEnabled;
    public static boolean gpsEnabled;

    /**
     * Classe permettant de vérifier les permissions nécessaires pour que l'application fonctionne
     *
     * @param context
     * @param allPermissionNeeded
     * @return
     */
    public static boolean hasPermissions(Context context, String... allPermissionNeeded) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context != null && allPermissionNeeded != null)
            for (String permission : allPermissionNeeded)
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
        return true;
    }

    /**
     * Vérifie sir le gps est activé
     *
     * @param activity
     */
    public static boolean checkGpsEnabled(final AppCompatActivity activity) {

        // test si GPS activè
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!gpsEnabled) {

            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage("GPS not enabled");
            dialog.setPositiveButton("Open location", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(myIntent);
                    //get gps
                }
            });

            dialog.show();
        }

        return gpsEnabled;
    }
}
