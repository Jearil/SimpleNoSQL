package com.colintmiller.simplenosql;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents any type of Query that can be preformed in SimpleNoSQL.
 */
public class NoSQLQuery<T> implements CancellableOperation {

    private DataDeserializer deserializer;
    private DataSerializer serializer;
    private String bucketId;
    private String entityId;
    private DataFilter<T> filter;
    private DataComparator<T> comparator;
    private List<OperationObserver> observers;
    private Class<T> clazz;
    private RetrievalCallback<T> callback;
    private NoSQLOperation operation;
    private List<NoSQLEntity<T>> entities;

    private boolean canceled = false;

    @Override
    public void cancel() {
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public enum NoSQLOperation {
        RETRIEVE,
        SAVE,
        DELETE
    }

    public NoSQLQuery(Class<T> clazz) {
        this.clazz = clazz;
        this.observers = new ArrayList<>();
    }

    public void setDeserializer(DataDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    public void setSerializer(DataSerializer serializer) {
        this.serializer = serializer;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setFilter(DataFilter<T> filter) {
        this.filter = filter;
    }

    public void setComparator(DataComparator<T> comparator) {
        this.comparator = comparator;
    }

    public void addObserver(OperationObserver observer) {
        observers.add(observer);
    }

    public String getBucketId() {
        return bucketId;
    }

    public String getEntityId() {
        return entityId;
    }

    public DataFilter<T> getFilter() {
        return filter;
    }

    public DataComparator<T> getComparator() {
        return comparator;
    }

    public List<OperationObserver> getObservers() {
        return observers;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public RetrievalCallback<T> getCallback() {
        return callback;
    }

    public NoSQLOperation getOperation() {
        return operation;
    }

    public List<NoSQLEntity<T>> getEntities() {
        return entities;
    }

    public void retrieve(RetrievalCallback<T> callback) {
        this.callback = callback;
        operation = NoSQLOperation.RETRIEVE;
    }

    public void delete() {
        operation = NoSQLOperation.DELETE;
    }

    public void save(List<NoSQLEntity<T>> entities) {
        this.entities = entities;
        operation = NoSQLOperation.SAVE;
    }

    public DataSerializer getSerializer() {
        if (serializer == null) {
            return new GsonSerialization();
        }
        return serializer;
    }

    public DataDeserializer getDeserializer() {
        if (deserializer == null) {
            return new GsonSerialization();
        }
        return deserializer;
    }

}
