package cz.muni.fi.pv239.testmeapp;

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
    private static SharedPreferences sharedPreferences;
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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = getLang(appContext);
        changeLang(appContext, language);
        changeLang(getBaseContext(), language);
    }

    public static void changeLang(Context context, String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Resources resources = context.getResources();
//        android.content.res.Configuration config = new android.content.res.Configuration();
        Configuration config = resources.getConfiguration();
        config.locale = myLocale;
        context.createConfigurationContext(config);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static String getLang(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(LANGUAGE_PREFERENCES, "en");
    }
}
