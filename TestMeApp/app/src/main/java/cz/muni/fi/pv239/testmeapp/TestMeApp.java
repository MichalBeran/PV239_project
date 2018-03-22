package cz.muni.fi.pv239.testmeapp;

import android.app.Application;

import io.realm.Realm;
/**
 * Created by Michal on 22.03.2018.
 */
public class TestMeApp
        extends Application {

    private static TestMeApp sInstance;

    public static TestMeApp getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        Realm.init(this);
    }
}
