package com.starfalling.ourfood;

import android.app.Application;

import com.starfalling.ourfood.model.Material;
import com.starfalling.ourfood.model.MaterialId;
import com.starfalling.ourfood.model.Purchase;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import io.realm.annotations.RealmModule;

public class OurFoodApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("ourfood.realm")
                .schemaVersion(4)
                .modules(new MyModule())
                .migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @RealmModule(classes = {Purchase.class, Material.class, MaterialId.class})
    public class MyModule {
    }

    public class MyMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            RealmSchema schema = realm.getSchema();
            if (oldVersion < 2) {
                schema.get("Purchase")
                        .addField("id", String.class);
                schema.get("Material")
                        .addIndex("id")
                        .addPrimaryKey("id");
            }
            if (oldVersion < 3) {
                schema.get("Purchase")
                        .addField("note", String.class);
            }
            if (oldVersion < 4) {
                schema.get("Purchase")
                        .addIndex("id")
                        .addPrimaryKey("id");
            }
        }
    }
}
