package com.colintmiller.simplenosql;


import android.content.Context;

/**
 * Simple access to a NoSQL store. You can save, retrieve, and delete any entity. Saving performs an insert
 * or an update (overwriting previous data). All calls are done asynchronously. Due to this, retrieving data
 * requires a callback.<p>
 *
 * Data is stored locally via serialization to a String. By default, the Gson library is used to convert objects to
 * JSON. You may provide custom serializers and deserializers by implementing the
 * {@link com.colintmiller.simplenosql.DataSerializer} and {@link com.colintmiller.simplenosql.DataDeserializer}
 * interfaces and supplying them on construction.
 */
public class NoSQL
{
    private static DataSerializer registeredSerializer;
    private static DataDeserializer registeredDeserializer;
    private static NoSQL singleton;

    private Context appContext;
    private DataSerializer singleSerializer;
    private DataDeserializer singleDeserializer;

    private NoSQL(Context context) {
        this.appContext = context.getApplicationContext();
    }

    /**
     * Get a builder for performing some sort of data operation. This builder can be used for retrieval, saving, or
     * deletion of data. See {@link com.colintmiller.simplenosql.QueryBuilder} for more information on how to build
     * a query.
     * @param context to use for this operation.
     * @param clazz related to this operation. This would be the class of the objects you're saving or retrieving.
     * @param <T> the type of the objects used in this operation.
     * @return a {@link com.colintmiller.simplenosql.QueryBuilder}.
     */
    public static <T> QueryBuilder<T> with(Context context, Class<T> clazz) {
        return withUsing(context, clazz, registeredSerializer, registeredDeserializer);
    }

    /**
     * Get a NoSQL instance based on the given Context. Will use the applicationContext of the given context for
     * requests. This instance is safe to save and use later with the {@link NoSQL#using(Class)} method to create a
     * QueryBuilder. You may also register serializers and deserializers with this instance. Note, an instance of NoSQL
     * will ignore the serializer and deserializer set with the 'register' static methods.
     *
     * @param context to use for future operations.
     * @return a NoSQL object for creating queries.
     */
    public static NoSQL with(Context context) {
        if (singleton == null) {
            synchronized (NoSQL.class) {
                if (singleton == null) {
                    singleton = new NoSQL(context);
                }
            }
        }

        return singleton;
    }

    /**
     * Get a builder for performing some sort of data operation. This builder can be used for retrieval, saving, or
     * deletion of data. See {@link com.colintmiller.simplenosql.QueryBuilder} for more information on how to build
     * a query.
     *
     * @param clazz related to this operation. This would be the class of the objects you're saving or retrieving.
     * @param <T> the type of the ojbets used in this operation.
     * @return a {@link com.colintmiller.simplenosql.QueryBuilder}.
     */
    public <T> QueryBuilder<T> using(Class<T> clazz) {
        return withUsing(appContext, clazz, singleSerializer, singleDeserializer);
    }

    /**
     * By default, SimpleNoSQL will use Google's Gson library for serialization and deserialization. You may override
     * the serializer to use via this method. If you override both the serializer and deserializer before making any
     * data calls, you may safely remove gson as a dependency.
     *
     * @param serializer to use with this NoSQL instance.
     * @return this for chaining.
     */
    public NoSQL withSerializer(DataSerializer serializer) {
        this.singleSerializer = serializer;
        return this;
    }

    /**
     * By default, SimpleNoSQL will use Google's Gson library for serialization and deserialization. You may override
     * the deserializer to use via this method. If you override both the serializer and deserializer before making any
     * data calls, you may safely remove gson as a dependency.
     *
     * @param deserializer to use with this NoSQL instance.
     * @return this for chaining.
     */
    public NoSQL withDeserializer(DataDeserializer deserializer) {
        this.singleDeserializer = deserializer;
        return this;
    }

    /**
     * By default, SimpleNoSQL will use Google's Gson library for serialization and deserialization. You may override
     * this globally here by registering a DataSerializer with whatever serialization method you'd like to use. If you
     * register both a Serializer and Deserializer before making any database calls, you may safely remove gson as
     * a dependency.
     * @param serializer to use in all future NoSQL requests.
     */
    public static void registerSerializer(DataSerializer serializer) {
        NoSQL.registeredSerializer = serializer;
    }

    /**
     * By default, SimpleNoSQL will use Google's Gson library for serialization and deserialization. You may override
     * this globally here by registering a DataSerializer with whatever serialization method you'd like to use. If you
     * register both a Serializer and Deserializer before making any database calls, you may safely remove gson as
     * a dependency.
     * @param deserializer to use in all future NoSQL requests.
     */
    public static void registerDeserializer(DataDeserializer deserializer) {
        NoSQL.registeredDeserializer = deserializer;
    }


    private static <T> QueryBuilder<T> withUsing(Context context, Class<T> clazz, DataSerializer serializer, DataDeserializer deserializer) {
        QueryBuilder<T> builder = new QueryBuilder<T>(context, clazz);
        if (serializer != null) {
            builder.serializer(serializer);
        }
        if (deserializer != null) {
            builder.deserializer(deserializer);
        }

        return builder;
    }
}
