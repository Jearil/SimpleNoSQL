package colintmiller.com.simplenosql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by cmiller on 7/30/14.
 */
public class NoSQLList implements Iterable<Object> {
    private List<Object> data;
    
    public NoSQLList() {
        data = new ArrayList<Object>();
    }

    public NoSQLList add(String value) {
        data.add(value);
        return this;
    }

    public NoSQLList add(Integer value) {
        data.add(value);
        return this;
    }

    public NoSQLList add(Boolean value) {
        data.add(value);
        return this;
    }

    public NoSQLList add(Float value) {
        data.add(value);
        return this;
    }

    public NoSQLList add(Double value) {
        data.add(value);
        return this;
    }

    public NoSQLList add(Long value) {
        data.add(value);
        return this;
    }

    public NoSQLList add(NoSQLList value) {
        data.add(value);
        return this;
    }

    public NoSQLList add(NoSQLMap value) {
        data.add(value);
        return this;
    }

    public NoSQLList createList() {
        NoSQLList list = new NoSQLList();
        data.add(list);
        return list;
    }

    public NoSQLMap createMap() {
        NoSQLMap map = new NoSQLMap();
        data.add(map);
        return map;
    }

    @Override
    public Iterator<Object> iterator() {
        return data.iterator();
    }
}
