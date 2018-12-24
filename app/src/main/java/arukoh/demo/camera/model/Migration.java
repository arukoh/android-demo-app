package arukoh.demo.camera.model;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            schema.create("Result")
                    .addField("id", String.class, FieldAttribute.REQUIRED)
                    .addField("timestamp", Date.class, FieldAttribute.REQUIRED)
                    .addField("score", Integer.class, FieldAttribute.REQUIRED)
            ;
            oldVersion++;
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Migration;
    }
}
