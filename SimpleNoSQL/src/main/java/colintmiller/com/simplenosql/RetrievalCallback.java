package colintmiller.com.simplenosql;

import java.util.List;

/**
 * Implement this callback to retrieve the results of a query operation from {@link colintmiller.com.simplenosql.NoSQL}.
 * The callback will be called on the UI thread so it is safe to call UI methods from within the callback.
 */
public interface RetrievalCallback {

    public void retrievedResults(List<NoSQLEntity> entities);
}
