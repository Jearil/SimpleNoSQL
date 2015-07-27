package com.colintmiller.simplenosql.toolbox;

import android.os.Looper;
import android.util.Log;
import com.colintmiller.simplenosql.NoSQLEntity;
import com.colintmiller.simplenosql.RetrievalCallback;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A special type of RetrievalCallback that can perform synchronous retrieval of data. Use it like you would any
 * RetrievalCallback, then call {@link SynchronousRetrieval#retrievedResults(java.util.List)} to get your results. Note
 * that this method is a blocking call and should not be done on the main thread.
 */
public class SynchronousRetrieval<T> implements RetrievalCallback<T> {
    public static final String TAG = SynchronousRetrieval.class.getName();
    private List<NoSQLEntity<T>> results = null;
    private CountDownLatch lock = new CountDownLatch(1);

    @Override
    public void retrievedResults(List<NoSQLEntity<T>> noSQLEntities) {
        results = noSQLEntities;
        lock.countDown();
    }

    /**
     * Synchronous blocking call to get results. You MUST have made a .retrieve() call with this object before calling
     * this method or it will timeout after 30 seconds and return a null result.
     * @return the results of your query, waiting until the results arrive.
     */
    public List<NoSQLEntity<T>> getSynchronousResults() {
        return getSynchronousResults(30, TimeUnit.SECONDS);
    }

    /**
     * Synchronous blocking call to get results with an additional timer option. You MUST have made a .retrieve() call
     * with this object before calling this method or it will timeout after the specified time and return a null result.
     * This call should never be made on the main thread.
     *
     * @param timeout the amount of time to wait for a timeout
     * @param unit the specified unit to apply to the timeout
     * @return the results of your query, waiting until the results arrive.
     */
    public List<NoSQLEntity<T>> getSynchronousResults(long timeout, TimeUnit unit) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            Log.e(TAG, "You should not be getting synchronous results from the UI thread!!");
        }
        try {
            lock.await(timeout, unit);
        } catch (InterruptedException e) {
            Log.e(TAG, "Unable to retrieve async results due to a timeout", e);
        }
        return results;
    }
}
