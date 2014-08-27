package com.colintmiller.simplenosql.threading;

import android.content.Context;
import android.os.Process;
import com.colintmiller.simplenosql.DataComparator;
import com.colintmiller.simplenosql.NoSQLEntity;
import com.colintmiller.simplenosql.NoSQLQuery;
import com.colintmiller.simplenosql.db.SimpleNoSQLDBHelper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Provides a thread for performing data operations based on a queue of operations.
 *
 * Queries are processed from the given queue and dispatched based on their operation
 * type. Retrieval queries have their results posted to the supplied callback in the
 * query. All operations also notify any observers. Both callback and observer
 * notification are handled by the {@link com.colintmiller.simplenosql.threading.QueryDelivery}
 * class which will post those results via the UI thread (or possibly a supplied thread).
 */
public class DataDispatcher extends Thread {

    private boolean hasQuit = false;
    private BlockingQueue<NoSQLQuery<?>> queue;
    private Context context;
    private QueryDelivery delivery;

    public DataDispatcher(BlockingQueue<NoSQLQuery<?>> queue, Context context, QueryDelivery delivery) {
        this.queue = queue;
        this.context = context;
        this.delivery = delivery;
    }

    /**
     * Forces the dispatcher to quit immediately. Any unprocessed queries in the queue
     * will not be processed.
     */
    public void quit() {
        hasQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        NoSQLQuery<?> query;

        // The exit for this loop is an InterruptedException
        while (true) {
            try {
                query = queue.take();

            } catch (InterruptedException e) {
                if (hasQuit) {
                    return;
                }
                continue;
            }

            if (query.isCanceled()) {
                // TODO: Add Logging of canceled request.
                continue;
            }

            SimpleNoSQLDBHelper helper = new SimpleNoSQLDBHelper(context, query.getSerializer(), query.getDeserializer());

            switch (query.getOperation()) {
                case SAVE:
                    save(query, helper);
                    break;
                case DELETE:
                    delete(query, helper);
                    break;
                case RETRIEVE:
                    retrieve(query, helper);
                    break;
                default:
                    throw new IllegalStateException("Should not have a null operation");
            }

            delivery.notifyObservers(query.getObservers());
        }
    }

    private <T> void save(NoSQLQuery<T> query, SimpleNoSQLDBHelper helper) {
        for (NoSQLEntity<T> entity : query.getEntities()) {
            helper.saveEntity(entity);
        }
    }

    private <T> void delete(NoSQLQuery<T> query, SimpleNoSQLDBHelper helper) {
        if (query.getBucketId() != null && query.getEntityId() != null) {
            helper.deleteEntity(query.getBucketId(), query.getEntityId());
        } else if (query.getBucketId() != null) {
            helper.deleteBucket(query.getBucketId());
        }
    }

    private <T> void retrieve(NoSQLQuery<T> query, SimpleNoSQLDBHelper helper) {
        if (query.getBucketId() != null && query.getEntityId() != null) {
            List<NoSQLEntity<T>> entityList = helper.getEntities(query.getBucketId(), query.getEntityId(), query.getClazz(), query.getFilter());
            sortAndDeliver(entityList, query);
        } else if (query.getBucketId() != null) {
            List<NoSQLEntity<T>> entityList = helper.getEntities(query.getBucketId(), query.getClazz(), query.getFilter());
            sortAndDeliver(entityList, query);
        }
    }

    private <T> void sortAndDeliver(List<NoSQLEntity<T>> entities, NoSQLQuery<T> query) {
        DataComparator<T> comparator = query.getComparator();
        if (comparator != null) {
            Collections.sort(entities, comparator);
        }
        delivery.performCallback(query.getCallback(), entities);
    }
}
