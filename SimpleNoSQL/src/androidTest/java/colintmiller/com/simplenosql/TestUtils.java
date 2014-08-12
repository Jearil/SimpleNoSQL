package colintmiller.com.simplenosql;

import android.content.Context;

/**
 * Created by cmiller on 8/12/14.
 */
public class TestUtils {

    public static void cleanBucket(String bucket, Context context) {
        NoSQLDeleteTask deleteTask = new NoSQLDeleteTask(context);
        deleteTask.doInBackground(bucket);
    }
}
