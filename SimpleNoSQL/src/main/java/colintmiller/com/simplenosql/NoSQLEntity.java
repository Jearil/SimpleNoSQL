package colintmiller.com.simplenosql;


import com.google.gson.Gson;

import java.util.List;
import java.util.Map;


/**
 * A generic entity that can store any set of data. To be used with the NoSQL class.
 */
public class NoSQLEntity<T> {
    private T data;
    private String id;
    private String bucket;

    public NoSQLEntity(String bucket, String id) {
        this(bucket, id, null);
    }

    public NoSQLEntity(String bucket, String id, T data) {
        this.bucket = bucket;
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public String getBucket() {
        return bucket;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

}
