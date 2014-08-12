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
    private GsonSerialization serialization;

    public NoSQLRetrieveTaskTest() {
        super(Activity.class);
        serialization = new GsonSerialization();
    }

    public void testGettingStoredData() {
        String bucketId = "bucket";
        String entityId = "entityId";

        NoSQLEntity<SampleBean> entity = getTestEntry(bucketId, entityId);

        NoSQLSaveTask saveTask = new NoSQLSaveTask(getInstrumentation().getTargetContext(), serialization);
        saveTask.doInBackground(entity);

        NoSQLRetrieveTask<SampleBean> retrieveTask = new NoSQLRetrieveTask<SampleBean>(getInstrumentation().getTargetContext(), null, serialization, SampleBean.class, null);
        List<NoSQLEntity<SampleBean>> retrievedEntities = retrieveTask.doInBackground(bucketId, entityId);

        assertNotNull("We should have retrieved the entities", retrievedEntities);
        assertEquals(1, retrievedEntities.size());
        NoSQLEntity<SampleBean> retEntity = retrievedEntities.get(0);
        assertNotNull("The retrieved entity should be non-null", retEntity);
        assertEquals(bucketId, retEntity.getBucket());
        assertEquals(entityId, retEntity.getId());
        assertEquals(entity.getData(), retEntity.getData());
        assertEquals(entity.getData().getName(), retEntity.getData().getName());
        assertEquals(4, retEntity.getData().getListing().size());
        List ids = retEntity.getData().getListing();
        for (int i = 0; i < ids.size(); i++) {
            assertEquals(String.class, ids.get(i).getClass());
            String id = (String) ids.get(i);
            assertEquals("ID" + i, id);
        }
    }

    public void testGettingFilteredResults() {
        String bucketId = "bucket";

        NoSQLEntity<SampleBean> entity1 = getTestEntry(bucketId, "entity1");
        NoSQLEntity<SampleBean> entity2 = getTestEntry(bucketId, "entity2");

        NoSQLSaveTask saveTask = new NoSQLSaveTask(getInstrumentation().getTargetContext(), serialization);
        saveTask.doInBackground(entity1, entity2);

        DataFilter<SampleBean> filter = new DataFilter<SampleBean>() {
            @Override
            public boolean isIncluded(NoSQLEntity<SampleBean> item) {
                return item.getId().equals("entity2");
            }
        };
        NoSQLRetrieveTask<SampleBean> retrieveTask = new NoSQLRetrieveTask<SampleBean>(getInstrumentation().getTargetContext(), null, serialization, SampleBean.class, filter);
        List<NoSQLEntity<SampleBean>> items = retrieveTask.doInBackground(bucketId);
        assertNotNull("The list of entities should not be null", items);
        assertEquals(1, items.size());
        assertEquals("entity2", items.get(0).getId());
    }

    //TODO: Add a test for getting all entities of a bucket

    private NoSQLEntity<SampleBean> getTestEntry(String bucketId, String entityId) {
        NoSQLEntity<SampleBean> entity = new NoSQLEntity<SampleBean>(bucketId, entityId);
        SampleBean bean = new SampleBean();
        bean.setName("SimpleNoSQL");
        List<String> ids = new ArrayList<String>(4);
        for (int i = 0; i < 4; i++) {
            ids.add("ID" + i);
        }
        bean.setListing(ids);
        entity.setData(bean);
        return entity;
    }
}
