package com.colintmiller.simplenosql;

import java.util.List;

/**
 * Implement this callback to retrieve the results of a query operation from {@link com.colintmiller.simplenosql.NoSQL}.
 * The callback will be called on the UI thread so it is safe to call UI methods from within the callback.
 */
public interface RetrievalCallback<T> {

    public void retrievedResults(List<NoSQLEntity<T>> entities);
}
