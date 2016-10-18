package com.colintmiller.simplenosql;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * <p>The main way of interacting with your data is via a QueryBuilder. The QueryBuilder can be used for both storage and
 * retrieval. You may also delete using the QueryBuilder. Additional parameters can be added using the various 'with'
 * or 'add' methods. Some methods only apply to one class of operation, which will be noted in the documentation of
 * each method.
 * <p>
 * A sample of a save operation:
 *
 * <pre>
 *     NoSQLEntity<String> entity = new NoSQLEntity<String>("bucket1", "string1");
 *     entity.setData("Some data to store");
 *     QueryBuilder<String> builder = new QueryBuilder<String>(context, String.class)
 *       .save(entity);
 * </pre>
 *
 * <p>A sample of a retrieval operation for that data:
 * <pre>
 *     QueryBuilder<String> builder = new QueryBuilder<String>(context, String.class)
 *       .bucketId("bucket1")
 *       .entityId("string1")
 *       .retrieve(new RetrievalCallback<String>() {
 *           public void retrievedResults(List<NoSQLEntity<String>> entities) {
 *              // display String to screen
 *              textView.setText(entities.get(0).getData());
 *           }
 *       });
 * </pre>
 *
 * <p>A sample deletion of that same data:
 * <pre>
 *     QueryBuilder<String> builder = new QueryBuilder<String>(context, String.class)
 *       .bucketId("bucket1")
 *       .entityId("string1")
 *       .delete();
 * </pre>
 *
 * <p>Save operations can have a single RetrievalCallback which runs on the UI thread. Any operation (including saving) can
 * have any number of {@link com.colintmiller.simplenosql.OperationObserver}'s that can be notified when the operation
 * is complete.
 */
public class QueryBuilder<T> {

    private NoSQLQuery<T> query;
    private BlockingQueue<NoSQLQuery<?>> dispatchQueue;

    /**
     * Construct a new QueryBuilder for performing a NoSQL operation.
     * @param clazz related to this operation.
     */
    public QueryBuilder(Class<T> clazz, BlockingQueue<NoSQLQuery<?>> queue) {
        this.query = new NoSQLQuery<>(clazz);
        this.dispatchQueue = queue;
    }

    /**
     * <p>Used in: RETRIEVAL, DELETION
     *
     * <p>The entityId of the entity to retrieve or delete. Note, a BucketId is REQUIRED for either of these operations.
     * Using only an EntityId will results in a no-op.
     *
     * @param entityId to use for retrieval or deletion.
     * @return this for chaining.
     */
    public QueryBuilder<T> entityId(String entityId) {
        query.setEntityId(entityId);
        return this;
    }

    /**
     * <p>Used in: RETRIEVAL, DELETION
     *
     * <p>The bucketId of the entity to retrieve for delete. This is REQUIRED for deletion or retrieval. You may further
     * narrow down a retrieval or deletion using an entityId to retrieve or delete a specific entity. For retrieval,
     * you may also narrow down the results by using a {@link com.colintmiller.simplenosql.DataFilter} instead.
     *
     * @param bucketId to use for retrieval or deletion of data.
     * @return this for chaining.
     */
    public QueryBuilder<T> bucketId(String bucketId) {
        query.setBucketId(bucketId);
        return this;
    }

    /**
     * <p>Used in: RETRIEVAL
     *
     * <p>An optional filter that can be used to filter results when retrieving data. A BucketId is still REQUIRED for
     * retrieving data. This merely applies the given filter to the returned data before passing it on to the Callback.
     *
     * @param filter to apply to the data when retrieving.
     * @return this for chaining.
     */
    public QueryBuilder<T> filter(DataFilter<T> filter) {
        query.setFilter(filter);
        return this;
    }

    /**
     * <p>Used in: RETRIEVAL
     *
     * <p>An optional comparator that can be used for ordering data that is returned. A BucketId is still REQUIRED for
     * retrieving data. This merely sorts the returned data using the given comparator.
     *
     * @param comparator to use to sort the retrieved data.
     * @return this for chaining.
     */
    public QueryBuilder<T> orderBy(DataComparator<T> comparator) {
        query.setComparator(comparator);
        return this;
    }

    /**
     * <p>Used in: RETRIEVAL
     *
     * <p>By default, SimpleNoSQL will used Google's Gson library to deserialize data. If you registered a
     * {@link com.colintmiller.simplenosql.DataDeserializer} with {@link NoSQL#withDeserializer(DataDeserializer)},
     * then this method will have already been called with that custom deserializer. You can use this to override what
     * deserializer will be used for data retrieval. If you always call this method (and
     * {@link com.colintmiller.simplenosql.QueryBuilder#serializer(DataSerializer)}), or you register both with
     * NoSQL, you may remove Gson as a dependency.
     *
     * @param deserializer to use for deserializing data
     * @return this for chaining.
     */
    public QueryBuilder<T> deserializer(DataDeserializer deserializer) {
        query.setDeserializer(deserializer);
        return this;
    }

    /**
     * <p>Used in: SAVE
     *
     * <p>By default, SimpleNoSQL will used Google's Gson library to serialize data. If you registered a
     * {@link com.colintmiller.simplenosql.DataSerializer} with {@link NoSQL#withSerializer(DataSerializer)},
     * then this method will have already been called with that custom serializer. You can use this to override what
     * serializer will be used for data retrieval. If you always call this method (and
     * {@link com.colintmiller.simplenosql.QueryBuilder#deserializer(DataDeserializer)}), or you register both with
     * NoSQL, you may remove Gson as a dependency.
     *
     * @param serializer to use for serializing data.
     * @return this for chaining.
     */
    public QueryBuilder<T> serializer(DataSerializer serializer) {
        query.setSerializer(serializer);
        return this;
    }

    /**
     * <p>Used in: SAVE, RETRIEVAL, DELETE
     *
     * <p>Adds an observer to the list of observers for this operation. When any operation is complete, all observers will
     * be triggered (in the order they were added). For retrieval tasks, observers are triggered AFTER data is delivered
     * to the RetrievalCallback.
     *
     * <p>If the operation is canceled before the operation finishes, the observers will not be triggered.
     *
     * @param observer to observe the finishing of this operation.
     * @return this for chaining.
     */
    public QueryBuilder<T> addObserver(OperationObserver observer) {
        query.addObserver(observer);
        return this;
    }

    /**
     * <p>Used in: RETRIEVAL
     *
     * <p>Perform a retrieve operation that will retrieve data from the datastore. Retrieval REQUIRES a bucketId to have
     * been set with this builder. If one is not supplied, the callback will be called with an empty list. In addition,
     * you may specific a specific entity using an EntityId, or filter all elements of a bucket by supplying a filter.
     * You may also have the results ordered by setting a DataComparator.
     *
     * <p>Results are returned using the passed in RetrievalCallback. This operation is cancelable, and if canceled
     * before the data is retrieved, the callback will not be called. In addition, after the callback has been called,
     * any OperationObservers that were registered will be triggered.
     *
     * @param callback to use when the data has been retrieved. The callback will be called on the UI thread.
     * @return a CancellableOperation for canceling the in-flight request before it's finished.
     */
    public CancellableOperation retrieve(RetrievalCallback<T> callback) {
        query.retrieve(callback);
        dispatchQueue.add(query);

        return query;
    }

    /**
     * <p>Used in: DELETE
     *
     * <p>Perform a deletion operation that will delete data from the datastore of the given Bucket. If only a bucket is
     * supplied, that entire bucket will be removed. If an entity is also supplied, only that entity will be removed.
     * The bucket is required, and if it is not provided the deletion will be a noop.
     *
     * <p>If any OperationObservers were registered, they will be triggered on the UI thread after the deletion is
     * complete.
     *
     * @return a CancellableOperation for canceling the in-flight request before it's finished.
     */
    public CancellableOperation delete() {
        query.delete();
        dispatchQueue.add(query);

        return query;
    }

    /**
     * <p>Used in: SAVE
     *
     * <p>Perform a save operation on the given NoSQLEntity. This will save the given entity to disk using the entities
     * bucket and entityId. If an entity already exists with the same ID in that bucket, this save will overwrite the
     * existing entity.
     *
     * <p>If any OperationObservers were registered, they will be triggered on the UI thread after the save is
     * complete.
     *
     * @param entity to save.
     * @return a CancellableOperation for canceling the in-flight request before it's finished.
     */
    public CancellableOperation save(NoSQLEntity<T> entity) {
        List<NoSQLEntity<T>> entities = new ArrayList<>(1);
        entities.add(entity);
        return save(entities);
    }

    /**
     * <p>Used in: SAVE
     *
     * <p>Perform a save operation on all of the given NoSQLEntity objects. This will save each entity to disk
     * using the entities bucket and entityId. If an entity already exists with the same ID in that bucket, this save
     * will overwrite the existing entity.
     *
     * <p>If any OperationObservers were registered, they will be triggered on the UI thread after the save is
     * complete.
     *
     * @param entities to save.
     * @return a CancellableOperation for canceling the in-flight request before it's finished.
     */
    public CancellableOperation save(List<NoSQLEntity<T>> entities) {
        query.save(entities);
        dispatchQueue.add(query);
        return query;
    }
}
