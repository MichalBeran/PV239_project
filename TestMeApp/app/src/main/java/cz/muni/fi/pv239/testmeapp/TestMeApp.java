package cz.muni.fi.pv239.testmeapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

import io.realm.Realm;
/**
 * Created by Michal on 22.03.2018.
 */
public class TestMeApp
        extends Application {

    public static Context appContext;
    private static TestMeApp sInstance;

    public static final String THEME_PREFERENCES = "pref_dark_theme";

    public static TestMeApp getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        appContext = getApplicationContext();

        Realm.init(this);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    public static Boolean isDarkThemeSet(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(THEME_PREFERENCES, false);
    }

    public static void setTheme(Activity activity){
        activity.setTheme(isDarkThemeSet(activity) ? R.style.AppThemeDark : R.style.AppTheme);
    }
}