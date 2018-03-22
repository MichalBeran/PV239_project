package cz.muni.fi.pv239.testmeapp.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Michal on 21.03.2018.
 */

public class Question extends RealmObject {
    public String text;
    public String type;
    public RealmList<Answer> answers;
}
