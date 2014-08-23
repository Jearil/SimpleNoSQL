package colintmiller.com.simplenosql;


import android.content.Context;

/**
 * Simple access to a NoSQL store. You can save, retrieve, and delete any entity. Saving performs an insert
 * or an update (overwriting previous data). All calls are done asynchronously. Due to this, retrieving data
 * requires a callback.<p>
 * <p/>
 * Data is stored locally via serialization to a String. By default, the Gson library is used to convert objects to
 * JSON. You may provide custom serializers and deserializers by implementing the
 * {@link colintmiller.com.simplenosql.DataSerializer} and {@link colintmiller.com.simplenosql.DataDeserializer}
 * interfaces and supplying them on construction.
 */
public class NoSQL {

    static NoSQL singleton = null;

    final Context context;
    final DataSerializer serializer;
    final DataDeserializer deserializer;

    NoSQL(Context context, DataSerializer serializer, DataDeserializer deserializer) {
        this.context = context;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    /**
     * Get a builder for performing some sort of data operation. This builder can be used for retrieval, saving, or
     * deletion of data. See {@link colintmiller.com.simplenosql.QueryBuilder} for more information on how to build
     * a query.
     *
     * @param clazz related to this operation. This would be the class of the objects you're saving or retrieving.
     * @param <T>   the type of the objects used in this operation.
     * @return a {@link colintmiller.com.simplenosql.QueryBuilder}.
     */
    public <T> QueryBuilder<T> using(Class<T> clazz) {
        QueryBuilder<T> builder = new QueryBuilder<T>(context, clazz);
        if (serializer != null) {
            builder.serializer(serializer);
        }
        if (deserializer != null) {
            builder.deserializer(deserializer);
        }

        return builder;
    }

    public static NoSQL with(Context context) {
        if (singleton == null) {
            synchronized (NoSQL.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                }
            }
        }
        return singleton;
    }

    public static class Builder {
        private final Context context;
        private DataSerializer serializer;
        private DataDeserializer deserializer;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder serializer(DataSerializer serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder deserializer(DataDeserializer deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        public NoSQL build() {
            if (serializer == null) {
                serializer = new GsonSerialization();
            }

            if (deserializer == null) {
                serializer = new GsonSerialization();
            }

            return new NoSQL(context, serializer, deserializer);
        }
    }
}
