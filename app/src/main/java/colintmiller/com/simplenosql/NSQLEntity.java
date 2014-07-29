package colintmiller.com.simplenosql;


import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A generic entity that can store any set of data. To be used with the NoSQL class.
 */
public class NSQLEntity
{
  private Map<String, Object> data;
  private String id;
  private String bucket;

  public NSQLEntity(String bucket, String id) {
    this.bucket = bucket;
    this.id = id;
    this.data = new HashMap<String, Object>();
  }

  public void setValue(String key, String value) {
    data.put(key, value);
  }

  public void setValue(String key, Integer value) {
    data.put(key, value);
  }

  public void setValue(String key, Float value) {
    data.put(key, value);
  }

  public void setValue(String key, Double value) {
    data.put(key, value);
  }

  public void setValue(String key, Long value) {
    data.put(key, value);
  }

  public void setValue(String key, Boolean value) {
    data.put(key, value);
  }

  public void setValue(String key, Map value) {
    data.put(key, value);
  }

  public void setValue(String key, List value) {
    data.put(key, value);
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

  public Map getMapValue(String key) {
    return getValue(key, Map.class);
  }

  @SuppressWarnings("unchecked")
  private <T> T getValue(String key, Class<T> clazz) {
    data.get(key);
    if (data.containsKey(key)) {
      try
      {
        return (T) data.get(key);
      } catch (ClassCastException e) {
        Log.w("NOSQLEntity", "Unable to cast class to desired type.", e);
      }
    }
    return null;
  }

  public String getId()
  {
    return id;
  }

  public String getBucket()
  {
    return bucket;
  }

  public NSQLEntity cloneTo(String bucket, String id) {
    NSQLEntity entity = new NSQLEntity(bucket, id);
    Map<String, Object> otherData = getNestedMap(this.data);
    entity.data = otherData;
    return entity;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getNestedMap(Map<String, Object> nestedData) {
    Map<String, Object> otherData = new HashMap<String, Object>(this.data.size());
    for (String key : nestedData.keySet()) {
      Object datum = nestedData.get(key);
      otherData.put(key, processItem(datum));
    }

    return otherData;
  }

  private Collection<Object> getNestedCollection(Collection<Object> nestedCollection) {
    Collection<Object> otherData = new ArrayList<Object>(nestedCollection.size());
    for (Object object : nestedCollection) {
      otherData.add(processItem(object));
    }
    return otherData;
  }

  @SuppressWarnings("unchecked")
  private Object processItem(Object datum) {
    if (datum instanceof Map) {
      return getNestedMap((Map<String, Object>) datum);
    } else if (datum instanceof Collection) {
      return getNestedCollection((Collection<Object>) datum);
    } else {
      // All primitive wrappers (and Strings) are immutable so no copy needed.
      return datum;
    }
  }
}
