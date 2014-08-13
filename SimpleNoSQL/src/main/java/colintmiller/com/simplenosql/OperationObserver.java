package colintmiller.com.simplenosql;

/**
 * An observer can be created to be notified when certain async operations complete, even if they don't return any
 * values (such as saving or deleting).
 */
public interface OperationObserver {

    /**
     * Implement to be notified when an operation has completed. For retrieval operations, this notification occurs
     * after the data has been delivered via {@link colintmiller.com.simplenosql.RetrievalCallback}
     */
    public void hasFinished();
}
