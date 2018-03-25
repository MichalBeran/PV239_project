package cz.muni.fi.pv239.testmeapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Locale;

import io.realm.Realm;
/**
 * Created by Michal on 22.03.2018.
 */
public class TestMeApp
        extends Application {

    public static Context context;
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
        context = getApplicationContext();

        Realm.init(this);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = getLang();
        changeLang(context, language);
    }

    public static void changeLang(Context context, String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static String getLang(){
        return sharedPreferences.getString(LANGUAGE_PREFERENCES, "en");
    }
}
