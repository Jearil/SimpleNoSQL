package com.colintmiller.simplenosql;

import com.google.gson.Gson;

/**
 * A {@link com.colintmiller.simplenosql.DataSerializer} and {@link com.colintmiller.simplenosql.DataDeserializer} that
 * uses the Gson library to transform objects into JSON and back. This is the default implementation used with
 * {@link com.colintmiller.simplenosql.NoSQL} if none are provided.
 */
public class GsonSerialization implements DataSerializer, DataDeserializer {

    private Gson gson;

    public GsonSerialization() {
        this.gson = new Gson();
    }

    public GsonSerialization(Gson gson) {
        this.gson = gson;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return gson.fromJson(new String(data), clazz);
    }

    @Override
    public <T> byte[] serialize(T data) {
        return gson.toJson(data).getBytes();
    }
}
