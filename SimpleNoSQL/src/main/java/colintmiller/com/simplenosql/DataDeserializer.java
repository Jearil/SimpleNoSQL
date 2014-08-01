package colintmiller.com.simplenosql;

/**
 * This interface allows the user to implement a custom DeSerializer for their data. Ultimately the data will come in
 * as a single String that must be converted into the desired object. {@link colintmiller.com.simplenosql.GsonSerialization}
 * implements this method and uses the Gson library for deserialization. Any deserialization method can be used however
 * as long as this interface is implemented.
 * <p>
 * This class is to be used with {@link colintmiller.com.simplenosql.NoSQL}.
 */
public interface DataDeserializer {

    public <T> T deserialize(String data, Class<T> clazz);
}
