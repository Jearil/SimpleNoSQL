package colintmiller.com.simplenosql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic tool used to convert a Map<String, Object> into actual objects.
 */
public class NoSQLGsonTools {

    private static Gson instance;
    public static Type mapType = new TypeToken<Map<String, Object>>(){}.getType();

    public static Gson getInstance() {
        if (instance == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(mapType, new NaturalDeserializer());
            instance = builder.create();
        }
        return instance;
    }


    private static class NaturalDeserializer implements JsonDeserializer<Map<String, Object>> {
        public Map<String, Object> deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context) {

            if(json.isJsonNull()) return null;
            else return handleObject(json.getAsJsonObject(), context);
        }
        private Object handlePrimitive(JsonPrimitive json) {
            if(json.isBoolean())
                return json.getAsBoolean();
            else if(json.isString())
                return json.getAsString();
            else {
                BigDecimal bigDec = json.getAsBigDecimal();
                // Find out if it is an int type
                try {
                    bigDec.toBigIntegerExact();
                    try { return bigDec.intValueExact(); }
                    catch(ArithmeticException e) {}
                    return bigDec.longValue();
                } catch(ArithmeticException e) {}
                // Just return it as a double
                return bigDec.doubleValue();
            }
        }
        private List<Object> handleArray(JsonArray json, JsonDeserializationContext context) {
            List<Object> array = new ArrayList<Object>(json.size());
            for(int i = 0; i < json.size(); i++) {
                JsonElement ele = json.get(i);
                if (ele.isJsonObject()) {
                    array.add(handleObject(ele.getAsJsonObject(), context));
                } else if (ele.isJsonPrimitive()) {
                    array.add(handlePrimitive(ele.getAsJsonPrimitive()));
                } else if (ele.isJsonArray()) {
                    array.add(handleArray(ele.getAsJsonArray(), context));
                } else if (ele.isJsonNull()) {
                    array.add(null);
                }
            }
            return array;
        }
        private Map<String, Object> handleObject(JsonObject json, JsonDeserializationContext context) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getValue().isJsonArray()) {
                    map.put(entry.getKey(), handleArray(entry.getValue().getAsJsonArray(), context));
                } else if (entry.getValue().isJsonPrimitive()) {
                    map.put(entry.getKey(), handlePrimitive(entry.getValue().getAsJsonPrimitive()));
                } else if (entry.getValue().isJsonObject()) {
                    map.put(entry.getKey(), context.deserialize(entry.getValue(), new TypeToken<Map<String, Object>>() {
                    }.getType()));
                } else if (entry.getValue().isJsonNull()) {
                    map.put(entry.getKey(), null);
                }
            }
            return map;
        }
    }
}
