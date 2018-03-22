package cz.muni.fi.pv239.testmeapp.model;

import io.realm.RealmObject;

/**
 * Created by Michal on 21.03.2018.
 */

public class Answer extends RealmObject{
    public String text;
    public boolean correct;
    public Integer points;
}
