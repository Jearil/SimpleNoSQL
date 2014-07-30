package colintmiller.com.simplenosql;


import android.content.Context;

/**
 * Simple access to a NoSQL store. You can save, retrieve, and delete any entity. Saving performs an insert
 * or an update (overwriting previous data). All calls are done asynchronously. Due to this, retrieving data
 * requires a callback.
 */
public class NoSQL
{
    private Context context;

    public NoSQL(Context context) {
        this.context = context;
    }

    public void save(NoSQLEntity entity) {
        NoSQLSaveTask saveTask = new NoSQLSaveTask(context);
        saveTask.execute(entity);
    }

    public <T> void getEntity(String bucket, String entityId, RetrievalCallback<T> callback, Class<T> clazz) {
        NoSQLRetrieveTask<T> retrieveTask = new NoSQLRetrieveTask<T>(context, callback, clazz);
        retrieveTask.execute(bucket, entityId);
    }

    public void deleteEntity(String bucket, String entityId) {
        NoSQLDeleteTask deleteTask = new NoSQLDeleteTask(context);
        deleteTask.execute(bucket, entityId);
    }

    public void deleteBucket(String bucket) {
        NoSQLDeleteTask deleteTask = new NoSQLDeleteTask(context);
        deleteTask.execute(bucket);
    }

    public <T> void getEntities(String bucket, RetrievalCallback<T> callback, Class<T> clazz) {
        NoSQLRetrieveTask<T> retrieveTask = new NoSQLRetrieveTask<T>(context, callback, clazz);
        retrieveTask.execute(bucket);
    }

}
