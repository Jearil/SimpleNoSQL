package com.colintmiller.simplenosql.db;

import android.content.Context;
import com.colintmiller.simplenosql.DataDeserializer;
import com.colintmiller.simplenosql.DataSerializer;

/**
 * A factory for choosing between different Data Stores. This will allow us to change the backend of SimpleNoSQL without
 * having to change much of how it works.
 */
public class SimpleDataStoreFactory {

    private DataStoreType type;

    public SimpleDataStoreFactory(DataStoreType type) {
        this.type = type;
    }

    public DataStore getDataStore(Context context, DataSerializer serializer, DataDeserializer deserializer) {
        switch (type) {
            case SQLITE:
            default:
                return new SimpleNoSQLDBHelper(context, serializer, deserializer);
        }
    }
}
