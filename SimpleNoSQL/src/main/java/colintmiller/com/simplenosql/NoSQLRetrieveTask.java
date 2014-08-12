package colintmiller.com.simplenosql;

import android.content.Context;
import android.os.AsyncTask;
import colintmiller.com.simplenosql.db.SimpleNoSQLDBHelper;

import java.util.List;

/**
 * Task used to retrieve data in the form of NoSQLEntity objects. This asyncTask takes 1 or 2 parameters. The first
 * parameter is always the bucket name to retrieve data from. The second parameter is optionally the entity id if you're
 * retrieving a specific entity. In either case, a List of results will be returned to the
 * {@link colintmiller.com.simplenosql.RetrievalCallback} that was passed in to get the results. The callback
 * will be called from the UI thread.
 */
public class NoSQLRetrieveTask<T> extends AsyncTask<String, Void, List<NoSQLEntity<T>>> {
    private Context context;
    private RetrievalCallback<T> callback;
    private Class<T> clazz;
    private DataDeserializer deserializer;
    private DataFilter<T> filter;

    public NoSQLRetrieveTask(Context context,
                             RetrievalCallback<T> callback,
                             DataDeserializer deserializer,
                             Class<T> clazz,
                             DataFilter<T> filter) {
        this.context = context;
        this.callback = callback;
        this.clazz = clazz;
        this.deserializer = deserializer;
        this.filter = filter;
    }

    @Override
    protected List<NoSQLEntity<T>> doInBackground(String... params) {
        SimpleNoSQLDBHelper helper = new SimpleNoSQLDBHelper(context, null, deserializer);
        if (params.length == 1) {
            return helper.getEntities(params[0], clazz, filter);
        } else if (params.length >= 2) {
            return helper.getEntities(params[0], params[1], clazz, filter);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<NoSQLEntity<T>> noSQLEntity) {
        callback.retrievedResults(noSQLEntity);
    }
}
