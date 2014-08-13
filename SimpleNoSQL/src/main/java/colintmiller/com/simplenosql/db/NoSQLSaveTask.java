package colintmiller.com.simplenosql.db;

import android.content.Context;
import android.os.AsyncTask;
import colintmiller.com.simplenosql.DataSerializer;
import colintmiller.com.simplenosql.NoSQLEntity;
import colintmiller.com.simplenosql.OperationObserver;
import colintmiller.com.simplenosql.db.SimpleNoSQLDBHelper;

import java.util.List;

/**
 * An asyncTask used to save one or more entities to the DB. All saving happens asynchronously. If an entity with the
 * same bucket and id already exists, this will act as an update (basically a delete and insert).
 *
 * TODO: You may provide a callback to be notified when the save is complete.
 */
public class NoSQLSaveTask<T> extends AsyncTask<Void, Void, Void> {

    private Context context;
    private DataSerializer serializer;
    private List<OperationObserver> observers;
    private List<NoSQLEntity<T>> entities;

    public NoSQLSaveTask(Context context, List<NoSQLEntity<T>> entities, DataSerializer serializer) {
        this(context, entities, serializer, null);
    }

    public NoSQLSaveTask(Context context, List<NoSQLEntity<T>> entities, DataSerializer serializer, List<OperationObserver> observers) {
        this.context = context;
        this.entities = entities;
        this.serializer = serializer;
        this.observers = observers;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (entities == null) {
            return null;
        }

        SimpleNoSQLDBHelper helper = new SimpleNoSQLDBHelper(context, serializer, null);
        for (NoSQLEntity entity : entities) {
            if (entity != null) {
                helper.saveEntity(entity);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (observers != null && !observers.isEmpty()) {
            for (OperationObserver observer : observers) {
                if (observer != null) {
                    observer.hasFinished();
                }
            }
        }
    }
}
