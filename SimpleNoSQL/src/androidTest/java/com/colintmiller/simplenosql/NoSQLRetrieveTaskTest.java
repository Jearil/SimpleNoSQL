package com.colintmiller.simplenosql;

import android.app.Activity;
import android.content.Context;
import android.test.ActivityUnitTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tests for the RetrieveTask for querying data from the DB. This includes querying a single entity or
 * an entire bucket.
 */
public class NoSQLRetrieveTaskTest extends ActivityUnitTestCase {
    private String bucketId;
    private CountDownLatch signal;
    private List<NoSQLEntity<SampleBean>> results;
    private Context context;

    public NoSQLRetrieveTaskTest() {
        super(Activity.class);
        bucketId = "retrieveTests";
        results = new ArrayList<>();
    }

    private RetrievalCallback<SampleBean> getCallback() {
        return new RetrievalCallback<SampleBean>() {
            @Override
            public void retrievedResults(List<NoSQLEntity<SampleBean>> noSQLEntities) {
                results.addAll(noSQLEntities);
                signal.countDown();
            }
        };
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.context = getInstrumentation().getTargetContext();

        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    signal = TestUtils.cleanBucket(bucketId, context);
                }
            });
        } catch (Throwable throwable) {
            // an error happened
            throw new Exception(throwable);
        }
        signal.await(2, TimeUnit.SECONDS);

        signal = new CountDownLatch(1);
    }

    public void testRetrievalBuilder() throws Throwable {
        NoSQLEntity<SampleBean> entity = getTestEntry(bucketId, "entity");
        SampleBean bean = new SampleBean();
        bean.setName("Colin");
        entity.setData(bean);

        saveBean(entity);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .bucketId(bucketId)
                        .retrieve(getCallback());
            }
        });

        signal.await(2, TimeUnit.SECONDS);
        assertFalse("We should have results", results.isEmpty());
    }

    public void testGettingStoredData() throws Throwable {
        final String entityId = "entityId";

        Runnable run = new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .bucketId(bucketId)
                        .entityId(entityId)
                        .retrieve(getCallback());
            }
        };
        gettingStoredDataWithRunnable(entityId, run);
    }

    private void gettingStoredDataWithRunnable(final String entityId, Runnable runnable) throws Throwable {
        NoSQLEntity<SampleBean> entity = getTestEntry(bucketId, entityId);
        saveBean(entity);

        runTestOnUiThread(runnable);

        signal.await(2, TimeUnit.SECONDS);

        assertNotNull("We should have retrieved the entities", results);
        assertEquals(1, results.size());
        NoSQLEntity<SampleBean> retEntity = results.get(0);
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

    public void testGettingFilteredResults() throws Throwable {

        NoSQLEntity<SampleBean> entity1 = getTestEntry(bucketId, "entity1");
        NoSQLEntity<SampleBean> entity2 = getTestEntry(bucketId, "entity2");

        saveBean(entity1, entity2);

        final DataFilter<SampleBean> filter = new DataFilter<SampleBean>() {
            @Override
            public boolean isIncluded(NoSQLEntity<SampleBean> item) {
                return item.getId().equals("entity2");
            }
        };

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .bucketId(bucketId)
                        .filter(filter)
                        .retrieve(getCallback());
            }
        });
        signal.await(2, TimeUnit.SECONDS);

        assertNotNull("The list of entities should not be null", results);
        assertEquals(1, results.size());
        assertEquals("entity2", results.get(0).getId());
    }

    public void testGettingOrderedResults() throws Throwable {

        List<NoSQLEntity<SampleBean>> entities = new ArrayList<>(5);

        for (int i = 0; i < 5; i++) {
            NoSQLEntity<SampleBean> data = new NoSQLEntity<>(bucketId, "entity" + i);
            SampleBean bean = new SampleBean();
            bean.setId(i + 1);
            data.setData(bean);
            entities.add(data);
        }

        saveBean(entities.toArray(new NoSQLEntity[1]));

        // we want unique ordering where odd numbers are lower than even numbers
        // [1,2,3,4,5] -> [1,3,5,2,4]
        final DataComparator<SampleBean> comparator = new DataComparator<SampleBean>() {
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

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .bucketId(bucketId)
                        .orderBy(comparator)
                        .retrieve(getCallback());
            }
        });

        signal.await(2, TimeUnit.SECONDS);

        assertNotNull("Should have returned a list", results);
        assertEquals("Should have 5 items in it", 5, results.size());
        assertEquals(1, results.get(0).getData().getId());
        assertEquals(3, results.get(1).getData().getId());
        assertEquals(5, results.get(2).getData().getId());
        assertEquals(2, results.get(3).getData().getId());
        assertEquals(4, results.get(4).getData().getId());
    }

    public void testNoBucket() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .retrieve(getCallback());
            }
        });
        signal.await(2, TimeUnit.SECONDS);

        assertTrue("Results should be empty", results.isEmpty());
    }


    public void testNoEntity() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .addObserver(getObserver())
                        .save(new NoSQLEntity<SampleBean>("null", "nullitem"));
            }
        });
        signal.await(2, TimeUnit.SECONDS);
        signal = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .bucketId("null")
                        .entityId("nullitem")
                        .retrieve(getCallback());
            }
        });
        signal.await(2, TimeUnit.SECONDS);

        assertFalse("Results should not be empty with a null entry", results.isEmpty());
        assertNull("Item should have been null", results.get(0).getData());

    }

    public void testOldData() throws Throwable {
        OldSampleBean oldBean = new OldSampleBean();
        oldBean.setName("Colin");
        oldBean.setField1("Developer");
        oldBean.setId(1);

        final NoSQLEntity<OldSampleBean> oldEntity = new NoSQLEntity<>("oldbucket", "old");
        oldEntity.setData(oldBean);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(OldSampleBean.class)
                        .addObserver(getObserver())
                        .save(oldEntity);
            }
        });
        signal.await(2, TimeUnit.SECONDS);
        signal = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .bucketId("oldbucket")
                        .entityId("old")
                        .retrieve(getCallback());
            }
        });
        signal.await(2, TimeUnit.SECONDS);

        assertFalse("Should have gotten results", results.isEmpty());
        SampleBean bean = results.get(0).getData();
        assertEquals("Name data should match between old and new", oldBean.getName(), bean.getName());
        assertEquals("Field1 data should match between old and new", oldBean.getField1(), bean.getField1());
        assertEquals("ID data should match between old and new", oldBean.getId(), bean.getId());
        assertNull("Old bean didn't have an innerBean, so this one shouldn't either", bean.getInnerBean());
    }

    private OperationObserver getObserver() {
        return new OperationObserver() {
            @Override
            public void hasFinished() {
                signal.countDown();;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private void saveBean(final NoSQLEntity... bean) throws Throwable {
        final List<NoSQLEntity<SampleBean>> beans = new ArrayList<>(bean.length);

        for (NoSQLEntity<SampleBean> aBean : bean) {
            beans.add(aBean);
        }

        final CountDownLatch saveLatch = new CountDownLatch(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .addObserver(new OperationObserver() {

                            @Override
                            public void hasFinished() {
                                saveLatch.countDown();
                            }
                        })
                        .save(beans);
            }
        });

        saveLatch.await(3, TimeUnit.SECONDS);
    }

    //TODO: Add a test for getting all entities of a bucket

    private NoSQLEntity<SampleBean> getTestEntry(String bucketId, String entityId) {
        NoSQLEntity<SampleBean> entity = new NoSQLEntity<>(bucketId, entityId);
        SampleBean bean = new SampleBean();
        bean.setName("SimpleNoSQL");
        List<String> ids = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            ids.add("ID" + i);
        }
        bean.setListing(ids);
        entity.setData(bean);
        return entity;
    }
}
