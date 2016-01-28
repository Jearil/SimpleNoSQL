package com.colintmiller.simplenosql;

/**
 * This interface allows the user to implement a custom Serializer for their data. Ultimately the data will come in
 * as a byte[] that must be converted into the desired object. {@link com.colintmiller.simplenosql.GsonSerialization}
 * implements this method and uses the Gson library for serialization. Any serialization method can be used however
 * as long as this interface is implemented.
 * <p>
 * This class is to be used with {@link com.colintmiller.simplenosql.NoSQL}.
 */
public interface DataSerializer {

    public <T> byte[] serialize(T data);
}
