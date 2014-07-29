package colintmiller.com.simplenosql;

import android.app.Activity;
import android.test.ActivityUnitTestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the RetrieveTask for querying data from the DB. This includes querying a single entity or
 * an entire bucket.
 */
public class NoSQLRetrieveTaskTest extends ActivityUnitTestCase {

    public NoSQLRetrieveTaskTest() {
        super(Activity.class);
    }

    public void testGettingStoredData() {
        String bucketId = "bucket";
        String entityId = "entityId";

        NoSQLEntity entity = getTestEntry(bucketId, entityId);

        NoSQLSaveTask saveTask = new NoSQLSaveTask(getInstrumentation().getTargetContext());
        saveTask.doInBackground(entity);

        NoSQLRetrieveTask retrieveTask = new NoSQLRetrieveTask(getInstrumentation().getTargetContext(), null);
        List<NoSQLEntity> retrievedEntities = retrieveTask.doInBackground(bucketId, entityId);

        assertNotNull("We should have retrieved the entities", retrievedEntities);
        assertEquals(1, retrievedEntities.size());
        NoSQLEntity retEntity = retrievedEntities.get(0);
        assertNotNull("The retrieved entity should be non-null", retEntity);
        assertEquals(bucketId, retEntity.getBucket());
        assertEquals(entityId, retEntity.getId());
        assertEquals(entity.jsonData(), retEntity.jsonData());
        assertEquals(entity.getStringValue("name"), retEntity.getStringValue("name"));
        assertEquals(4, retEntity.getListValue("ids").size());
        List ids = retEntity.getListValue("ids");
        for (int i = 0; i < ids.size(); i++) {
            assertEquals(String.class, ids.get(i).getClass());
            String id = (String) ids.get(i);
            assertEquals("ID" + i, id);
        }
    }

    //TODO: Add a test for getting all entities of a bucket

    private NoSQLEntity getTestEntry(String bucketId, String entityId) {
        NoSQLEntity entity = new NoSQLEntity(bucketId, entityId);
        entity.setValue("name", "SimpleNoSQL");
        List<String> ids = new ArrayList<String>(4);
        for (int i = 0; i < 4; i++) {
            ids.add("ID" + i);
        }
        entity.setValue("ids", ids);
        return entity;
    }
}
