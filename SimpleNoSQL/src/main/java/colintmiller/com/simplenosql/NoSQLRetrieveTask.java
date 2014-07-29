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
public class NoSQLRetrieveTask extends AsyncTask<String, Void, List<NoSQLEntity>> {
    private Context context;
    private RetrievalCallback callback;

    public NoSQLRetrieveTask(Context context, RetrievalCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected List<NoSQLEntity> doInBackground(String... params) {
        SimpleNoSQLDBHelper helper = new SimpleNoSQLDBHelper(context);
        if (params.length == 1) {
            return helper.getEntities(params[0]);
        } else if (params.length >= 2) {
            return helper.getEntities(params[0], params[1]);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<NoSQLEntity> noSQLEntity) {
        callback.retrievedResults(noSQLEntity);
    }
}
