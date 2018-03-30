package cz.muni.fi.pv239.testmeapp.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.TestMeApp;

/**
 * Created by Michal on 25.03.2018.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String LANGUAGE_PREFERENCES = "pref_selected_language";

    @NonNull
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        Preference initPref = findPreference(LANGUAGE_PREFERENCES);
        initPref.setSummary(initPref.getSharedPreferences().getString(LANGUAGE_PREFERENCES, ""));
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(LANGUAGE_PREFERENCES)) {
            Preference pref = findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, ""));
            TestMeApp.changeLang(TestMeApp.appContext, pref.getSharedPreferences().getString(key, "en"));
            TestMeApp.changeLang(getActivity().getBaseContext(), pref.getSharedPreferences().getString(key, "en"));
            TestMeApp.changeLang(getActivity().getApplicationContext(), pref.getSharedPreferences().getString(key, "en"));
            getActivity().recreate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
