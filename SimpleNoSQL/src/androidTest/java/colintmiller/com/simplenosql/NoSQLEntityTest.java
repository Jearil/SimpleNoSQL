package colintmiller.com.simplenosql;


import android.app.Activity;
import android.test.ActivityUnitTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Tests for NSQLEntity and it's various functions.
 */
public class NoSQLEntityTest extends ActivityUnitTestCase {
    public NoSQLEntityTest() {
        super(Activity.class);
    }

    public void testCloneEntity() {
        NoSQLEntity original = new NoSQLEntity("default", "first");
        original.setValue("name", "NoSql");
        original.setValue("id", "different id");
        original.setValue("someBoolean", false);
        List<String> originalList = new ArrayList<String>();
        originalList.add("first");
        originalList.add("second");
        original.setValue("list", originalList);
        Map<String, Boolean> originalMap = new HashMap<String, Boolean>();
        originalMap.put("first", true);
        originalMap.put("second", false);
        original.setValue("map", originalMap);

        NoSQLEntity duplicate = original.cloneTo("change", "second");
        assertEquals(duplicate.getStringValue("name"), "NoSql");
        assertEquals(duplicate.getStringValue("id"), "different id");
        assertEquals(duplicate.getBooleanValue("someBoolean"), Boolean.FALSE);
        assertNull(original.getStringValue("list"));
        assertEquals(original.getListValue("list"), duplicate.getListValue("list"));
        assertNull(original.getStringValue("map"));
        assertEquals(original.getMapValue("map"), duplicate.getMapValue("map"));
    }

    public void testWrongType() {
        NoSQLEntity data = new NoSQLEntity("default", "first");
        data.setValue("string", "Some String");
        assertNull("Should get null when asking for an integer where a String resides.", data.getIntegerValue("string"));
    }

    public void testEntityJson() {
        NoSQLEntity data = new NoSQLEntity("default", "first");
        data.setValue("name", "SimpleNoSQL");
        String json = data.jsonData();
        assertEquals("{\"name\":\"SimpleNoSQL\"}", json);
    }

    public void testDeepJson() {
        String json = "{\"ids\":[\"ID1\",\"ID2\",\"ID3\"]}";
        NoSQLEntity entity = new NoSQLEntity("default", "first");
        entity.setJsonData(json);
        assertNotNull("There should be an 'ids' list: " + entity.jsonData(), entity.getListValue("ids"));
    }
}
