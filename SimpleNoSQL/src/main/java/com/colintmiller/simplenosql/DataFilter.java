package com.colintmiller.simplenosql;


/**
 * An filter can be applied to a query to limit what results are returned.
 */
public interface DataFilter<T> {

    /**
     * Determines if a given item should be included in the results.
     * @param item to possibly filter
     * @return true if the item should exist in the list, false if it should be filtered out.
     */
    public boolean isIncluded(NoSQLEntity<T> item);
}
