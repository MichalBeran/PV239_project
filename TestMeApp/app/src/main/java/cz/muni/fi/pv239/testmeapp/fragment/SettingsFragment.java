package cz.muni.fi.pv239.testmeapp.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cz.muni.fi.pv239.testmeapp.R;

/**
 * Created by Michal on 25.03.2018.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String THEME_PREFERENCES = "pref_dark_theme";
    public static final String GIT_USER = "pref_git_user";
    public static final String GIT_REPO = "pref_git_repo";
    public static final String GIT_FOLDER = "pref_git_folder";

    @NonNull
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(THEME_PREFERENCES)) {
            getActivity().recreate();
        }
        updatePreferencesSummary(sharedPreferences, key);
    }

    private void updatePreferencesSummary(SharedPreferences sharedPreferences, String key){
        Preference preference = findPreference(key);
        if (key.equals(GIT_USER)){
            EditTextPreference editText = (EditTextPreference) preference;
            preference.setSummary(editText.getText());
        }
        if (key.equals(GIT_REPO)){
            EditTextPreference editText = (EditTextPreference) preference;
            preference.setSummary(editText.getText());
        }
        if (key.equals(GIT_FOLDER)){
            EditTextPreference editText = (EditTextPreference) preference;
            preference.setSummary(editText.getText());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        updatePreferencesSummary(getPreferenceScreen().getSharedPreferences(), GIT_USER);
        updatePreferencesSummary(getPreferenceScreen().getSharedPreferences(), GIT_REPO);
        updatePreferencesSummary(getPreferenceScreen().getSharedPreferences(), GIT_FOLDER);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
