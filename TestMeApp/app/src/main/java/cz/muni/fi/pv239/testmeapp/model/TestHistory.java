package cz.muni.fi.pv239.testmeapp.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class TestHistory extends RealmObject {
    @PrimaryKey
    @Required
    public String id;
    @Required
    public String testURL;
    @Required
    public Date date;
    public int points;

}
