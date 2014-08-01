package colintmiller.com.simplenosql;

import android.content.Context;
import android.os.AsyncTask;
import colintmiller.com.simplenosql.db.SimpleNoSQLDBHelper;

/**
 * An asyncTask used to save one or more entities to the DB. All saving happens asynchronously. If an entity with the
 * same bucket and id already exists, this will act as an update (basically a delete and insert).
 *
 * TODO: You may provide a callback to be notified when the save is complete.
 */
public class NoSQLSaveTask extends AsyncTask<NoSQLEntity, Void, Void> {

    private Context context;
    private DataSerializer serializer;

    public NoSQLSaveTask(Context context, DataSerializer serializer) {
        this.context = context;
        this.serializer = serializer;
    }

    @Override
    protected Void doInBackground(NoSQLEntity... params) {
        SimpleNoSQLDBHelper helper = new SimpleNoSQLDBHelper(context, serializer, null);
        for (NoSQLEntity entity : params) {
            if (entity != null) {
                helper.saveEntity(entity);
            }
        }

        return null;
    }
}
