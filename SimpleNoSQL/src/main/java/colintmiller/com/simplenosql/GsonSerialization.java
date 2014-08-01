package colintmiller.com.simplenosql;

import com.google.gson.Gson;

/**
 * A {@link colintmiller.com.simplenosql.DataSerializer} and {@link colintmiller.com.simplenosql.DataDeserializer} that
 * uses the Gson library to transform objects into JSON and back. This is the default implementation used with
 * {@link colintmiller.com.simplenosql.NoSQL} if none are provided.
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
    public <T> T deserialize(String data, Class<T> clazz) {
        return gson.fromJson(data, clazz);
    }

    @Override
    public <T> String serialize(T data) {
        return gson.toJson(data);
    }
}
