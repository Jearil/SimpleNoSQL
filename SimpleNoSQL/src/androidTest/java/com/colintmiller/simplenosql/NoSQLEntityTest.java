package com.colintmiller.simplenosql;


import android.app.Activity;
import android.test.ActivityUnitTestCase;

import java.util.ArrayList;
import java.util.List;


/**
 * Tests for NSQLEntity and it's various functions.
 */
public class NoSQLEntityTest extends ActivityUnitTestCase {
    private GsonSerialization serialization;

    public NoSQLEntityTest() {
        super(Activity.class);
        serialization = new GsonSerialization();
    }

    public void testJsonConversion() {
        SampleBean testData = new SampleBean();
        testData.setName("Colin");
        testData.setId(1);
        SampleBean innerData = new SampleBean();
        innerData.setName( "Developer" );
        innerData.setField1("Of things");
        List<String> someList = new ArrayList<String>();
        someList.add("item 1");
        someList.add("item 2");
        innerData.setListing(someList);

        NoSQLEntity<SampleBean> entity = new NoSQLEntity<SampleBean>("bucket", "id");
        entity.setData(testData);

        String jsonData = serialization.serialize(entity.getData());

        NoSQLEntity<SampleBean> hydrated = new NoSQLEntity<SampleBean>("bucket", "id2");
        hydrated.setData(serialization.deserialize(jsonData, SampleBean.class));
        SampleBean waterData = hydrated.getData();

        assertEquals(testData, waterData);
    }

    public void testUUID() {
        SampleBean testData = new SampleBean();
        testData.setName("Colin");

        NoSQLEntity<SampleBean> entity = new NoSQLEntity<SampleBean>("bucket");
        assertNotNull(entity.getId());
    }
}
