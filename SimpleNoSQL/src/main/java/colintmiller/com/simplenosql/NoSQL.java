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
    private Context context;
    private DataSerializer serializer;
    private DataDeserializer deserializer;

    /**
     * Create a NoSQL instance using the default {@link colintmiller.com.simplenosql.GsonSerialization} serializer and
     * deserializer.
     * @param context for accessing the local system to save and retrieve data.
     */
    public NoSQL(Context context) {
        this.context = context;
        GsonSerialization gsonSerialization = new GsonSerialization();
        this.serializer = gsonSerialization;
        this.deserializer = gsonSerialization;
    }

    /**
     * Create a NoSQL instance using the default {@link colintmiller.com.simplenosql.DataDeserializer} but with a
     * custom {@link colintmiller.com.simplenosql.DataSerializer}. Since the deserializer uses Gson, you should probably
     * make sure that your serializer creates valid JSON data. Consider using
     * {@link colintmiller.com.simplenosql.NoSQL#NoSQL(android.content.Context, DataSerializer, DataDeserializer)}
     * instead.
     * @param context for accessing the local system to save and retrieve data.
     * @param serializer to use for serializing your data.
     */
    public NoSQL(Context context, DataSerializer serializer) {
        this(context);
        this.serializer = serializer;
    }

    /**
     * Create a NoSQL instance using the default {@link colintmiller.com.simplenosql.DataSerializer} but with a
     * custom {@link colintmiller.com.simplenosql.DataDeserializer}. Since the serializer uses Gson, you should probably
     * make sure that your deserializer creates valid JSON data. Consider using
     * {@link colintmiller.com.simplenosql.NoSQL#NoSQL(android.content.Context, DataSerializer, DataDeserializer)}
     * instead.
     * @param context for accessing the local system to save and retrieve data.
     * @param deserializer to use for deserializing your data.
     */
    public NoSQL(Context context, DataDeserializer deserializer) {
        this(context);
        this.deserializer = deserializer;
    }

    /**
     * Create a NoSQL instance using your own {@link colintmiller.com.simplenosql.DataSerializer} and
     * {@link colintmiller.com.simplenosql.DataDeserializer}. This will allow you to replace the default Gson serializer
     * and deserializer with your own implementation (like Jackson perhaps). If your app always provides it's own
     * serializer and deserializer, you can exclude the Gson dependency in the app to save space as it will never be
     * used.
     * @param context for accessing the local system to save and retrieve data.
     * @param serializer to use for serializing your data.
     * @param deserializer to use for deserializing your data.
     */
    public NoSQL(Context context, DataSerializer serializer, DataDeserializer deserializer) {
        this.context = context;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    /**
     * Save the given Entity to the datastore. All saves create new records or replace old ones. Saving is done
     * asynchronously so this method will return immediately.
     * @param entity to save.
     */
    public void save(NoSQLEntity entity) {
        NoSQLSaveTask saveTask = new NoSQLSaveTask(context, serializer);
        saveTask.execute(entity);
    }

    /**
     * Retrieve a particular entity defined by the bucket and entity ids. Retrieval is done Asynchronously and so this
     * method will return immediately. The provided callback will have it's
     * {@link colintmiller.com.simplenosql.RetrievalCallback#retrievedResults(java.util.List)} method called with the
     * results. The result's will be a list with either 0 or 1 item in it (if the entity exists or not).
     * @param bucket that the entity exists in.
     * @param entityId is the id of the specific entity to retrieve.
     * @param callback used for processing the results. This will be called from the UI thread.
     * @param clazz is the class that this entity belongs to. This will be used for deserialization.
     * @param <T> is the Type of the data being retrieved.
     */
    public <T> void getEntity(String bucket, String entityId, RetrievalCallback<T> callback, Class<T> clazz) {
        NoSQLRetrieveTask<T> retrieveTask = new NoSQLRetrieveTask<T>(context, callback, deserializer, clazz, null);
        retrieveTask.execute(bucket, entityId);
    }

    /**
     * Delete a specific entity given a bucket and entity ID. This will delete the entity if it exists, otherwise it
     * will act as a noop. Deletion happens Asynchronously, so this method will return immediately.
     * @param bucket that the specific entity resides in.
     * @param entityId is the id of the entity to delete.
     */
    public void deleteEntity(String bucket, String entityId) {
        NoSQLDeleteTask deleteTask = new NoSQLDeleteTask(context);
        deleteTask.execute(bucket, entityId);
    }

    /**
     * Delete all of the entities of an entire bucket. This will delete all entities in that bucket asynchronously, so
     * this method will return immediately.
     * @param bucket to delete the contents of.
     */
    public void deleteBucket(String bucket) {
        NoSQLDeleteTask deleteTask = new NoSQLDeleteTask(context);
        deleteTask.execute(bucket);
    }

    /**
     * Retrieves all of the entities belonging to a specific bucket. Entities must be of the same type. Ordering of the
     * returned entities is not guaranteed. Retrieval is done Asynchronously and so this method will return immediately.
     * The provided callback will have it's {@link colintmiller.com.simplenosql.RetrievalCallback#retrievedResults(java.util.List)}
     * method called with the results.
     * @param bucket to retrieve all entities from
     * @param callback used for processing the results. This will be called from the UI thread.
     * @param clazz is the class that the entity belongs to. This will be used for deserialization.
     * @param <T> is the Type of the data being retrieved.
     */
    public <T> void getEntities(String bucket, RetrievalCallback<T> callback, Class<T> clazz) {
        getEntities(bucket, callback, clazz, null);
    }

    /**
     * Retrieves all of the entities belonging to a specific bucket. Entities must be of the same type. Ordering of the
     * returned entities is not guaranteed. Retrieval is done Asynchronously and so this method will return immediately.
     * The provided callback will have it's {@link colintmiller.com.simplenosql.RetrievalCallback#retrievedResults(java.util.List)}
     * method called with the results. Optional DataFilter will filter the results.
     * @param bucket to retrieve all entities from
     * @param callback used for processing the results. This will be called from the UI thread.
     * @param clazz is the class that the entity belongs to. This will be used for deserialization.
     * @param filter an optional filter that can exclude bucket items from the resultSet.
     * @param <T> is the Type of the data being retrieved.
     */
    public <T> void getEntities(String bucket, RetrievalCallback<T> callback, Class<T> clazz, DataFilter<T> filter) {
        NoSQLRetrieveTask<T> retrieveTask = new NoSQLRetrieveTask<T>(context, callback, deserializer, clazz, filter);
    }

}
