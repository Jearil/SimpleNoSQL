package com.colintmiller.simplenosql;

/**
 * An operation which can be canceled. If you need to cancel an operation (which will prevent the callbacks from being
 * called), you can call cancel() on that operation.
 */
public interface CancellableOperation {

    public void cancel();
}
