package cz.muni.fi.pv239.testmeapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

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
        String language = getLang();
        changeLang(appContext, language);
    }

    public static void changeLang(Context context, String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.createConfigurationContext(config);
        }else {
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
//        Resources resources = context.getResources();
//        Configuration configuration = resources.getConfiguration();
//        android.content.res.Configuration configuration = new android.content.res.Configuration();
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
//            configuration.setLocale(myLocale);
//        } else{
//            configuration.locale=myLocale;
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//            context.createConfigurationContext(configuration);
//        } else {
//            context.getResources().updateConfiguration(configuration,displayMetrics);
//        }
    }

    public static String getLang(){
        return sharedPreferences.getString(LANGUAGE_PREFERENCES, "en");
    }
}
