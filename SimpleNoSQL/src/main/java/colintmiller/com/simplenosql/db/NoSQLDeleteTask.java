package colintmiller.com.simplenosql.db;

import android.content.Context;
import android.os.AsyncTask;
import colintmiller.com.simplenosql.OperationObserver;
import colintmiller.com.simplenosql.db.SimpleNoSQLDBHelper;

import java.util.List;

/**
 * The AsyncTask that will be used to delete one or more entities from the DB. There are two possible
 * parameters you can pass when running this task. The first parameter is always the bucket. The second
 * parameter is an optional entityId. If you supply both, that specific entity will be removed. If you
 * supply only the bucket, the contents of the bucket will be removed. This is done asynchronously.
 *
 * TODO: Optionally you can provide a callback to perform some action when this operation is completed.
 */
public class NoSQLDeleteTask extends AsyncTask<String, Void, Void> {

    private Context context;
    private List<OperationObserver> observers;

    public NoSQLDeleteTask(Context context, List<OperationObserver> observers) {
        this.context = context;
        this.observers = observers;
    }

    public NoSQLDeleteTask(Context context) {
        this(context, null);
    }

    @Override
    protected Void doInBackground(String... params) {
        SimpleNoSQLDBHelper helper = new SimpleNoSQLDBHelper(context, null, null);
        if (params.length == 1) {
            helper.deleteBucket(params[0]);
        } else if (params.length == 2) {
            helper.deleteEntity(params[0], params[1]);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (observers != null && !observers.isEmpty()) {
            for (OperationObserver observer : observers) {
                observer.hasFinished();
            }
        }
    }
}
