package cz.muni.fi.pv239.testmeapp.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Michal on 21.03.2018.
 */

public class Test extends RealmObject{
    public String url;
    public String name;
    public Integer testCount;
    public String testDuration;
    public Integer testMinPoint;
    public RealmList<Question> questions;
}
