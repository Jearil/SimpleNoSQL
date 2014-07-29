package colintmiller.com.simplenosql;


import android.app.Activity;
import android.test.ActivityUnitTestCase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Tests for NSQLEntity
 */
public class NSQLEntityTest extends ActivityUnitTestCase
{
  public NSQLEntityTest()
  {
    super(Activity.class);
  }

  public void testCloneEntity() {
    NSQLEntity original = new NSQLEntity("default", "first");
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

    NSQLEntity duplicate = original.cloneTo("change", "second");
    assertEquals(duplicate.getStringValue("name"), "NoSql");
    assertEquals(duplicate.getStringValue("id"), "different id");
    assertEquals(duplicate.getBooleanValue("someBoolean"), Boolean.FALSE);
    assertNotSame(original.getListValue("list"), duplicate.getListValue("list"));
    assertEquals(original.getListValue("list"), duplicate.getListValue("list"));
    assertNotSame(original.getMapValue("map"), duplicate.getMapValue("map"));
    assertEquals(original.getMapValue("map"), duplicate.getMapValue("map"));
  }

  public void testWrongType() {
    NSQLEntity data = new NSQLEntity("default", "first");
    data.setValue("string", "Some String");
    assertNull("Should get null when asking for an integer where a String resides.", data.getIntegerValue("string"));
  }
}
