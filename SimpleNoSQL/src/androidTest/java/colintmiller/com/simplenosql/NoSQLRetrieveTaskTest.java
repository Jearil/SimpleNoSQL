package colintmiller.com.simplenosql;

import android.app.Activity;
import android.test.ActivityUnitTestCase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Tests for the RetrieveTask for querying data from the DB. This includes querying a single entity or
 * an entire bucket.
 */
public class NoSQLRetrieveTaskTest extends ActivityUnitTestCase {
    private GsonSerialization serialization;
    private String bucketId;

    public NoSQLRetrieveTaskTest() {
        super(Activity.class);
        serialization = new GsonSerialization();
        bucketId = "retrieveTests";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        TestUtils.cleanBucket(bucketId, getInstrumentation().getTargetContext());
    }

    public void testGettingStoredData() {
        String entityId = "entityId";

        NoSQLEntity<SampleBean> entity = getTestEntry(bucketId, entityId);

        NoSQLSaveTask saveTask = new NoSQLSaveTask(getInstrumentation().getTargetContext(), serialization);
        saveTask.doInBackground(entity);

        NoSQLRetrieveTask<SampleBean> retrieveTask = new NoSQLRetrieveTask<SampleBean>(getInstrumentation().getTargetContext(), null, serialization, SampleBean.class, null, null);
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
        NoSQLRetrieveTask<SampleBean> retrieveTask = new NoSQLRetrieveTask<SampleBean>(getInstrumentation().getTargetContext(), null, serialization, SampleBean.class, filter, null);
        List<NoSQLEntity<SampleBean>> items = retrieveTask.doInBackground(bucketId);
        assertNotNull("The list of entities should not be null", items);
        assertEquals(1, items.size());
        assertEquals("entity2", items.get(0).getId());
    }

    public void testGettingOrderedResults() {

        List<NoSQLEntity<SampleBean>> entities = new ArrayList<NoSQLEntity<SampleBean>>(5);

        for (int i = 0; i < 5; i++) {
            NoSQLEntity<SampleBean> data = new NoSQLEntity<SampleBean>(bucketId, "entity" + i);
            SampleBean bean = new SampleBean();
            bean.setId(i + 1);
            data.setData(bean);
            entities.add(data);
        }

        NoSQLSaveTask saveTask = new NoSQLSaveTask(getInstrumentation().getTargetContext(), serialization);
        saveTask.doInBackground(entities.toArray(new NoSQLEntity[1]));

        // we want unique ordering where odd numbers are lower than even numbers
        // [1,2,3,4,5] -> [1,3,5,2,4]
        DataComparator<SampleBean> comparator = new DataComparator<SampleBean>() {
            @Override
            public int compare(NoSQLEntity<SampleBean> lhs, NoSQLEntity<SampleBean> rhs) {
                int lhsid = lhs.getData().getId();
                int rhsid = rhs.getData().getId();

                if (lhsid % 2 == 0) { // even
                    if (rhsid % 2 == 0) { //both even
                        return lhsid - rhsid;
                    } else { // right is odd, it's considered "smaller"
                        return 1;
                    }
                } else { // left is odd
                    if (rhsid % 2 == 0) { // right is even, it's considered "larger"
                        return -1;
                    } else { // both are odd
                        return lhsid - rhsid;
                    }
                }
            }
        };

        NoSQLRetrieveTask<SampleBean> retrieveTask = new NoSQLRetrieveTask<SampleBean>(getInstrumentation().getTargetContext(), null, serialization, SampleBean.class, null, comparator);
        List<NoSQLEntity<SampleBean>> beans = retrieveTask.doInBackground(bucketId);
        assertNotNull("Should have returned a list", beans);
        assertEquals("Should have 5 items in it", 5, beans.size());
        assertEquals(1, beans.get(0).getData().getId());
        assertEquals(3, beans.get(1).getData().getId());
        assertEquals(5, beans.get(2).getData().getId());
        assertEquals(2, beans.get(3).getData().getId());
        assertEquals(4, beans.get(4).getData().getId());
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
