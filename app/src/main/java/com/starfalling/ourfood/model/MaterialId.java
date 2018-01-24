package com.starfalling.ourfood.model;

import io.realm.RealmObject;

/**
 * Created by USER on 2018/1/24.
 */

public class MaterialId extends RealmObject{
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
