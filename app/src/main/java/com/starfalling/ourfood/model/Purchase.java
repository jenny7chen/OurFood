package com.starfalling.ourfood.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Purchase extends RealmObject{
    private long spent;
    private long time;
    private RealmList<MaterialId> materialIds;

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
}
