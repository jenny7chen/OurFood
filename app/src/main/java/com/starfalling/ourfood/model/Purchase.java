package com.starfalling.ourfood.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Purchase extends RealmObject{

    @PrimaryKey
    private String id;
    private long spent;
    private long time;
    private String note;
    private RealmList<MaterialId> materialIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSpent() {
        return spent;
    }

    public void setSpent(long spent) {
        this.spent = spent;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public RealmList<MaterialId> getMaterialIds() {
        return materialIds;
    }

    public void setMaterialIds(RealmList<MaterialId> materialIds) {
        this.materialIds = materialIds;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
