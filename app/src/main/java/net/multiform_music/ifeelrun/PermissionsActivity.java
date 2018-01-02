package net.multiform_music.ifeelrun;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import net.multiform_music.ifeelrun.helper.PermissionsHelper;
import net.multiform_music.ifeelrun.helper.PlayNotificationHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michel.dio on 04/05/2017.
 *
 * Activité d'entrée permettant de déterminer sir les permissions nécessaires sont activées :
 *
 * - Location
 * - GPS
 *
 */

public class PermissionsActivity extends AppCompatActivity {

    private static final int PERMISSION_ALL = 0;
    private Handler h;
    private Runnable r;

    private static boolean firstCreate = true;
    private static boolean fine_location = false;
    private String[] PERMISSIONS = { android.Manifest.permission.ACCESS_FINE_LOCATION };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        PlayNotificationHelper.initplayNotification(this);

        h = new Handler();

        r = new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(PermissionsActivity.this, HomeActivity.class));
                finish();
            }
        };

        // liste des permissions à vérifier
        String[] PERMISSIONS = { android.Manifest.permission.ACCESS_FINE_LOCATION };

        if(!PermissionsHelper.hasPermissions(this, PERMISSIONS)){

            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

        } else if (!PermissionsHelper.checkGpsEnabled(this)) {

       }
        else {

            h.postDelayed(r, 1500);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        int index = 0;

        Map<String, Integer> PermissionsMap = new HashMap<String, Integer>();
        for (String permission : permissions) {
            PermissionsMap.put(permission, grantResults[index]);
            index++;
        }

        if ((PermissionsMap.get(android.Manifest.permission.ACCESS_FINE_LOCATION) != 0)) {

            Toast.makeText(this, "Location permissions are a must", Toast.LENGTH_LONG).show();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            startActivity(new Intent(PermissionsActivity.this, PermissionsActivity.class));

        } else {

            fine_location = true;
        }


        /*else {

            h.postDelayed(r, 1500);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        // si on revient de l'écran d'activation GPS on resteste l'état
        if (!firstCreate && PermissionsHelper.hasPermissions(this, PERMISSIONS)) {
            if (PermissionsHelper.checkGpsEnabled(this)) {
                startActivity(new Intent(PermissionsActivity.this, HomeActivity.class));
            }
        }
        firstCreate = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}

