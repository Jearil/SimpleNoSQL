package com.colintmiller.simplenosql.db;

import android.provider.BaseColumns;

/**
 * The basic contract for our DB's that SimpleNoSQL will use to store user data.
 */
public class SimpleNoSQLContract {
    private SimpleNoSQLContract() {}

    public static abstract class EntityEntry implements BaseColumns {
        public static final String TABLE_NAME = "simplenosql";
        public static final String COLUMN_NAME_BUCKET_ID = "bucketid";
        public static final String COLUMN_NAME_ENTITY_ID = "entityid";
        public static final String COLUMN_NAME_DATA = "data";
    }
}
