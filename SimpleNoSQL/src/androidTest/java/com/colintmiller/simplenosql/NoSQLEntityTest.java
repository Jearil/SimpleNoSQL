package com.colintmiller.simplenosql;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Tests for NSQLEntity and it's various functions.
 */
@RunWith(AndroidJUnit4.class)
public class NoSQLEntityTest {
    private GsonSerialization serialization;

    public NoSQLEntityTest() {
        serialization = new GsonSerialization();
    }

    @Test
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

        byte[] jsonData = serialization.serialize(entity.getData());

        NoSQLEntity<SampleBean> hydrated = new NoSQLEntity<SampleBean>("bucket", "id2");
        hydrated.setData(serialization.deserialize(jsonData, SampleBean.class));
        SampleBean waterData = hydrated.getData();

        assertEquals(testData, waterData);
    }

    @Test
    public void testUUID() {
        SampleBean testData = new SampleBean();
        testData.setName("Colin");

        NoSQLEntity<SampleBean> entity = new NoSQLEntity<SampleBean>("bucket");
        assertNotNull(entity.getId());
    }
}
