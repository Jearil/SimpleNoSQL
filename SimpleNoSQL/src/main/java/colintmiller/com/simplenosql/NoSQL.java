package colintmiller.com.simplenosql;


import android.content.Context;

/**
 * Simple access to a NoSQL store. You can save, retrieve, and delete any entity. Saving performs an insert
 * or an update (overwriting previous data). All calls are done asynchronously. Due to this, retrieving data
 * requires a callback.<p>
 *
 * Data is stored locally via serialization to a String. By default, the Gson library is used to convert objects to
 * JSON. You may provide custom serializers and deserializers by implementing the
 * {@link colintmiller.com.simplenosql.DataSerializer} and {@link colintmiller.com.simplenosql.DataDeserializer}
 * interfaces and supplying them on construction.
 */
public class NoSQL
{
    private static DataSerializer serializer;
    private static DataDeserializer deserializer;


    /**
     * Get a builder for performing some sort of data operation. This builder can be used for retrieval, saving, or
     * deletion of data. See {@link colintmiller.com.simplenosql.QueryBuilder} for more information on how to build
     * a query.
     * @param context to use for this operation.
     * @param clazz related to this operation. This would be the class of the objects you're saving or retrieving.
     * @param <T> the type of the objects used in this operation.
     * @return a {@link colintmiller.com.simplenosql.QueryBuilder}.
     */
    public static <T> QueryBuilder<T> with(Context context, Class<T> clazz) {
        QueryBuilder<T> builder = new QueryBuilder<T>(context, clazz);
        if (serializer != null) {
            builder.withSerializer(serializer);
        }
        if (deserializer != null) {
            builder.withDeserializer(deserializer);
        }

        return builder;
    }

    /**
     * By default, SimpleNoSQL will use Google's Gson library for serialization and deserialization. You may override
     * this globally here by registering a DataSerializer with whatever serialization method you'd like to use. If you
     * register both a Serializer and Deserializer before making any database calls, you may safely remove gson as
     * a dependency.
     * @param serializer to use in all future NoSQL requests.
     */
    public static void registerSerializer(DataSerializer serializer) {
        NoSQL.serializer = serializer;
    }

    /**
     * By default, SimpleNoSQL will use Google's Gson library for serialization and deserialization. You may override
     * this globally here by registering a DataSerializer with whatever serialization method you'd like to use. If you
     * register both a Serializer and Deserializer before making any database calls, you may safely remove gson as
     * a dependency.
     * @param deserializer to use in all future NoSQL requests.
     */
    public static void registerDeserializer(DataDeserializer deserializer) {
        NoSQL.deserializer = deserializer;
    }
}
