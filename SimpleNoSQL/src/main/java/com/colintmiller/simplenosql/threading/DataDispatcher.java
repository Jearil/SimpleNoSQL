package com.colintmiller.simplenosql.threading;

import android.content.Context;
import android.os.Process;
import com.colintmiller.simplenosql.DataComparator;
import com.colintmiller.simplenosql.NoSQLEntity;
import com.colintmiller.simplenosql.NoSQLQuery;
import com.colintmiller.simplenosql.db.DataStore;
import com.colintmiller.simplenosql.db.DataStoreType;
import com.colintmiller.simplenosql.db.SimpleDataStoreFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    private ConcurrentHashMap<String, ReadWriteLock> locks;
    private SimpleDataStoreFactory dataStoreFactory;

    public DataDispatcher(
            BlockingQueue<NoSQLQuery<?>> queue,
            Context context,
            QueryDelivery delivery,
            ConcurrentHashMap<String, ReadWriteLock> locks,
            DataStoreType type) {
        this.queue = queue;
        this.context = context;
        this.delivery = delivery;
        this.locks = locks;
        this.dataStoreFactory = new SimpleDataStoreFactory(type);
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

            DataStore dataStore = dataStoreFactory.getDataStore(
                    context,
                    query.getSerializer(),
                    query.getDeserializer());

            switch (query.getOperation()) {
                case SAVE:
                    save(query, dataStore);
                    break;
                case DELETE:
                    delete(query, dataStore);
                    break;
                case RETRIEVE:
                    retrieve(query, dataStore);
                    break;
                default:
                    throw new IllegalStateException("Should not have a null operation");
            }

            delivery.notifyObservers(query.getObservers());
        }
    }

    private <T> void save(NoSQLQuery<T> query, DataStore dataStore) {
        obtainWriteLock(query.getBucketId());
        for (NoSQLEntity<T> entity : query.getEntities()) {
            dataStore.saveEntity(entity);
        }
        releaseWriteLock(query.getBucketId());
    }

    private <T> void delete(NoSQLQuery<T> query, DataStore dataStore) {
        String bucket = query.getBucketId();
        
        obtainWriteLock(bucket);
        if (bucket != null && query.getEntityId() != null) {
            dataStore.deleteEntity(bucket, query.getEntityId());
        } else if (bucket != null) {
            dataStore.deleteBucket(bucket);
        }
        releaseWriteLock(bucket);
    }

    private <T> void retrieve(NoSQLQuery<T> query, DataStore dataStore) {
        String bucket = query.getBucketId();
        
        obtainReadLock(bucket);
        if (bucket != null && query.getEntityId() != null) {
            List<NoSQLEntity<T>> entityList = dataStore.getEntities(bucket, query.getEntityId(), query.getClazz(), query.getFilter());
            sortAndDeliver(entityList, query);
        } else if (bucket != null) {
            List<NoSQLEntity<T>> entityList = dataStore.getEntities(bucket, query.getClazz(), query.getFilter());
            sortAndDeliver(entityList, query);
        }
        releaseReadLock(bucket);
    }

    private <T> void sortAndDeliver(List<NoSQLEntity<T>> entities, NoSQLQuery<T> query) {
        DataComparator<T> comparator = query.getComparator();
        if (comparator != null) {
            Collections.sort(entities, comparator);
        }
        delivery.performCallback(query.getCallback(), entities);
    }

    private void obtainReadLock(String bucket) {
        if (bucket != null) {
            ReadWriteLock lock = getReadWriteLock(bucket);
            lock.readLock().lock();
        }
    }
    
    private void releaseReadLock(String bucket) {
        if (bucket != null) {
            ReadWriteLock lock = getReadWriteLock(bucket);
            lock.readLock().unlock();
        }
    }
    
    private void obtainWriteLock(String bucket) {
        if (bucket != null) {
            ReadWriteLock lock = getReadWriteLock(bucket);
            lock.writeLock().lock();
        }
    }
    
    private void releaseWriteLock(String bucket) {
        if (bucket != null) {
            ReadWriteLock lock = getReadWriteLock(bucket);
            lock.writeLock().unlock();
        }
    }

    private ReadWriteLock getReadWriteLock(String bucket) {
        if (!locks.containsKey(bucket)) {
            ReadWriteLock newLock = new ReentrantReadWriteLock();
            ReadWriteLock possibleLock = locks.putIfAbsent(bucket, newLock);

            // if a value already exists (from another thread) we need to use that one
            if (possibleLock != null) {
                return possibleLock;
            }
            return newLock;
        }
        return locks.get(bucket);
    }
}
