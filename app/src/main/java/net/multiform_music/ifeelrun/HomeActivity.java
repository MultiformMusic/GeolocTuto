package net.multiform_music.ifeelrun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.Window;

import net.multiform_music.ifeelrun.helper.DatabaseHelper;
import net.multiform_music.ifeelrun.helper.ParamHelper;

/**
 * Created by Michel on 06/05/2017.
 *
 */

public class HomeActivity extends AppCompatActivity implements FragmentHomeMenu.HomeMenuClickListener {

    FragmentHomeMenu fragmentHomeMenu;
    FragmentHomeLastPerformance fragmentHomeLastPerformance;
    FragmentMaps fragmentMaps;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_home);

        /*TextView tollBarTitle = (TextView)findViewById(R.id.toolbar_title);

        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/basset_italic.ttf");
        tollBarTitle.setTypeface(face);
        */

        // fragment menu
        fragmentHomeMenu = FragmentHomeMenu.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_menu_container, fragmentHomeMenu, "homeMenuFragment")
                .commit();


        // fragment last performance
        fragmentHomeLastPerformance = FragmentHomeLastPerformance.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_last_performance_container, fragmentHomeLastPerformance, "homeLastPerformanceFragment")
                .commit();

        // fragment de la carte
        fragmentMaps = FragmentMaps.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, fragmentMaps, "mapFragment")
                .commit();


        // récupération du paramétrage carte
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        ParamHelper.mapsType = settings.getString("mapsType","normal");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // positionnement du type de maps
        fragmentMaps.setMapType(ParamHelper.mapsType);

        // positionnement du last id running pour que la carte affiche le path de la dernière performance
        fragmentMaps.setIdRunningLastPerformance(new DatabaseHelper(this).getLastIdRecordRunning());
    }

    @Override
    public void onClickRunning() {

        Intent intentRunning = new Intent(this, RunningActivity.class);
        startActivityForResult(intentRunning, ParamHelper.backCodeActivityRunning);
    }

    @Override
    public void onClickStatistic() {

    }

    @Override
    public void onClickParameter() {

        Intent intentSettings = new Intent(this, SettingsActivity.class);
        startActivity(intentSettings);
    }
}
