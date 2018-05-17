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
    public static final String LANGUAGE_PREFERENCES = "pref_selected_language";
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
        String language = getLang(appContext);
        changeLang(appContext, language);
        changeLang(getBaseContext(), language);
    }

    public static void changeLang(Context context, String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = myLocale;
        context.createConfigurationContext(config);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static String getLang(Context context) {
        String usedLang = PreferenceManager.getDefaultSharedPreferences(context).getString(LANGUAGE_PREFERENCES, null);
        if (usedLang == null) {
            usedLang = context.getResources().getConfiguration().locale.getLanguage();
            if (!(usedLang.equals("en") || usedLang.equals("cs"))) {
                usedLang = "en";
            }
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            pref.edit().putString(LANGUAGE_PREFERENCES, usedLang).commit();
        }
        return usedLang;
    }

    public static Boolean isDarkThemeSet(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(THEME_PREFERENCES, false);
    }

    public static void setTheme(Activity activity){
        activity.setTheme(isDarkThemeSet(activity) ? R.style.AppThemeDark : R.style.AppTheme);
    }
}