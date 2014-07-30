package colintmiller.com.simplenosql;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cmiller on 7/30/14.
 */
public class NoSQLMap {
    private Map<String, Object> data;

    public NoSQLMap() {
        data = new HashMap<String, Object>();
    }

    public NoSQLMap setValue(String key, String value) {
        data.put(key, value);
        return this;
    }

    public NoSQLMap setValue(String key, Integer value) {
        data.put(key, value);
        return this;
    }

    public NoSQLMap setValue(String key, Float value) {
        data.put(key, value);
        return this;
    }

    public NoSQLMap setValue(String key, Double value) {
        data.put(key, value);
        return this;
    }

    public NoSQLMap setValue(String key, Long value) {
        data.put(key, value);
        return this;
    }

    public NoSQLMap setValue(String key, Boolean value) {
        data.put(key, value);
        return this;
    }

    public NoSQLMap setValue(String key, NoSQLMap value) {
        data.put(key, value);
        return this;
    }

    public NoSQLMap setValue(String key, NoSQLList value) {
        data.put(key, value);
        return this;
    }

    public String getStringValue(String key) {
        return getValue(key, String.class);
    }

    public Integer getIntegerValue(String key) {
        return getValue(key, Integer.class);
    }

    public Boolean getBooleanValue(String key) {
        return getValue(key, Boolean.class);
    }

    public Float getFloatValue(String key) {
        return getValue(key, Float.class);
    }

    public Double getDoubleValue(String key) {
        return getValue(key, Double.class);
    }

    public Long getLongValue(String key) {
        return getValue(key, Long.class);
    }

    public List getListValue(String key) {
        return getValue(key, List.class);
    }

    public NoSQLMap createMap(String key) {
        NoSQLMap node = new NoSQLMap();
        data.put(key, node);
        return node;
    }

    public NoSQLList createList(String key) {
        NoSQLList list = new NoSQLList();
        data.put(key, list);
        return list;
    }

    public NoSQLMap getMapValue(String key) {
        return getValue(key, NoSQLMap.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(String key, Class<T> clazz) {
        data.get(key);
        if (data.containsKey(key)) {
            try {
                T item = (T) data.get(key);
                if (clazz.isAssignableFrom(item.getClass())) {
                    return item;
                }
            } catch (ClassCastException e) {
                Log.w("NOSQLEntity", "Unable to cast class to desired type.", e);
            }
        }
        return null;
    }
}
