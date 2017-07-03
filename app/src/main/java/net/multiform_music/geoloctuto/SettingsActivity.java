package net.multiform_music.geoloctuto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import net.multiform_music.geoloctuto.helper.ParamHelper;

/**
 * Created by michel.dio on 16/05/2017.
 *
 */

public class SettingsActivity extends PreferenceActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.app_preferences);

            //if you are using default SharedPreferences
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            onSharedPreferenceChanged(sharedPrefs, "unitVelocity");
            onSharedPreferenceChanged(sharedPrefs, "unitDistance");
            onSharedPreferenceChanged(sharedPrefs, "unitElevation");
            onSharedPreferenceChanged(sharedPrefs, "unitHeight");
            onSharedPreferenceChanged(sharedPrefs, "unitWeight");
            onSharedPreferenceChanged(sharedPrefs, "mapsType");
        }

        @Override
        public void onResume() {
            super.onResume();

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();

            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            Preference pref = findPreference(key);

            if (pref instanceof ListPreference) {

                ListPreference listPref = (ListPreference) pref;
                CharSequence summarySeq = listPref.getSummary();
                String summary = summarySeq.toString();

                if (summary.contains(":")) {
                    summary = summary.substring(0, summary.indexOf(":")).trim();
                }

                pref.setSummary(Html.fromHtml("<font color='red'>" +  summary + " : " + listPref.getValue() + "</font>"));
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intentHome = new Intent(this, HomeActivity.class);
        startActivity(intentHome);
    }
}
