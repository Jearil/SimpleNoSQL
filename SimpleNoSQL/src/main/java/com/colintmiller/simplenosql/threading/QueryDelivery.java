package com.colintmiller.simplenosql.threading;

import android.os.Handler;
import com.colintmiller.simplenosql.NoSQLEntity;
import com.colintmiller.simplenosql.OperationObserver;
import com.colintmiller.simplenosql.RetrievalCallback;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Delivers query results onto the appropriate thread.
 */
public class QueryDelivery {

    private final Executor poster;

    /**
     * Creates a new QueryDelivery which will post to the given handler.
     * @param handler {@link Handler} to post query results on.
     */
    public QueryDelivery(final Handler handler) {
        poster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    /**
     * Notifies observers that the operation has finished.
     * @param observers to notify.
     */
    public void notifyObservers(List<OperationObserver> observers) {
        for (final OperationObserver observer : observers) {
            if (observer != null) {
                poster.execute(new Runnable() {
                    @Override
                    public void run() {
                        observer.hasFinished();
                    }
                });
            }
        }
    }

    /**
     * Sends query results to the given callback.
     * @param callback to call via the handler
     * @param entities to return to the callback
     * @param <T> type of data being returned to the callback
     */
    public <T> void performCallback(final RetrievalCallback<T> callback, final List<NoSQLEntity<T>> entities) {
        poster.execute(new Runnable() {
            @Override
            public void run() {
                callback.retrievedResults(entities);
            }
        });
    }
}
