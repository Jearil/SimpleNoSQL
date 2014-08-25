package com.colintmiller.simplenosql;

import java.util.Comparator;

/**
 * Compare 2 NoSQLEntities of the same type. Used for ordering of results.
 */
public interface DataComparator<T> extends Comparator<NoSQLEntity<T>> {
}
