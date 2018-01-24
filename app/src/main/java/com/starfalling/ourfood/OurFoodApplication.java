package com.starfalling.ourfood;

import android.app.Application;

import com.starfalling.ourfood.model.Material;
import com.starfalling.ourfood.model.MaterialId;
import com.starfalling.ourfood.model.Purchase;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.annotations.RealmModule;

public class OurFoodApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("ourfood.realm")
                .schemaVersion(1)
                .modules(new MyModule())
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @RealmModule(classes = { Purchase.class, Material.class, MaterialId.class})
    public class MyModule {
    }
}
