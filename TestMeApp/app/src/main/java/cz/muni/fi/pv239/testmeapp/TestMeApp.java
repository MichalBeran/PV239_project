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
    public static final String GIT_USER = "pref_git_user";
    public static final String GIT_REPO = "pref_git_repo";
    public static final String GIT_FOLDER = "pref_git_folder";

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

    public static String getGitUser(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(GIT_USER, appContext.getString(R.string.pref_git_user_default));
    }

    public static String getGitRepo(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(GIT_REPO, appContext.getString(R.string.pref_git_repo_default));
    }

    public static String getGitFolder(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(GIT_FOLDER, appContext.getString(R.string.pref_git_folder_default));
    }
}