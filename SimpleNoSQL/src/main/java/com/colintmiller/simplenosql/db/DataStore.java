package com.colintmiller.simplenosql.db;

import com.colintmiller.simplenosql.DataFilter;
import com.colintmiller.simplenosql.NoSQLEntity;

import java.util.List;

/**
 * A DataStore provides access to the data that SimpleNoSQL stores. The implementation of how this is stored (in a
 * database, on file, in memory.. etc) is up to the implementation. Basic CRD operations are provided through a
 * DataStore. Updates are not supported, and are expected to occur by saving an entity using the same bucket and id as
 * what is being replaced.
 */
public interface DataStore {

    /**
     * Save the given entity to the DataStore. The data should be stored in an associated bucket with the associated id
     * so that calls to getEntities, deleteEntity, and deleteBucket would apply to them.
     *
     * @param entity to be stored.
     * @param <T> the type of the object being stored.
     */
    <T> void saveEntity(NoSQLEntity<T> entity);

    /**
     * Delete a given entity from a given bucket. Future calls to getEntities for this bucket/entityId combination
     * should not return results. If the
     *
     * @param bucket to delete an entity from
     * @param entityId the entity to be deleted.
     * @return true if an entity was deleted, false otherwise
     */
    boolean deleteEntity(String bucket, String entityId);

    /**
     * Delete the contents of a given bucket. Future calls to getEntities with this bucket should return an empty list.
     * @param bucket to delete all entities from
     * @return true if any entities were deleted, false if nothing was deleted (because there were no entities in
     * this bucket)
     */
    boolean deleteBucket(String bucket);

    /**
     * Get an entity of the specified bucket and entityId and return an object of the given class. This also runs the
     * given filter. If the entity does not pass the filter, an empty list will be returned.
     *
     * @param bucket to retrieve the entity from
     * @param entityId of the entity to be retrieved
     * @param clazz class of the given entity.  All entities in a bucket should be the same class.
     * @param filter is an optional filter to apply to the entity
     * @param <T> type of the object to be returned.
     * @return a List of NoSQLEntity objects that will contain either the specified entity, or be empty.
     */
    <T> List<NoSQLEntity<T>> getEntities(String bucket, String entityId, Class<T> clazz, DataFilter<T> filter);

    /**
     * Get all entities of the specified bucket. If a filter is supplied, also filter the results. If there are no
     * entities in the given bucket, or if the filter excludes all of them, an empty list will be returned.
     *
     * @param bucket to retrieve all entities from
     * @param clazz class of the given entity. All entities in a bucket should be the same class.
     * @param filter is the optional filter to apply to the entity.
     * @param <T> type of the object to be returned.
     * @return a List of NoSQLEntity objects containing the filtered entities of the given bucket.
     */
    <T> List<NoSQLEntity<T>> getEntities(String bucket, Class<T> clazz, DataFilter<T> filter);
}
