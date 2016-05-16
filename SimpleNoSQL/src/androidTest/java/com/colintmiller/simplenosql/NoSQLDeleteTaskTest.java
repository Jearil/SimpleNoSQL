package com.colintmiller.simplenosql;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityUnitTestCase;
import com.colintmiller.simplenosql.db.SimpleNoSQLContract;
import com.colintmiller.simplenosql.db.SimpleNoSQLDBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tests to verify the deletion asyncTask performs as expected
 */
public class NoSQLDeleteTaskTest extends ActivityUnitTestCase {
    private GsonSerialization serialization;
    private Context context;
    private CountDownLatch signal;

    public NoSQLDeleteTaskTest() {
        super(Activity.class);
        serialization = new GsonSerialization();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = getInstrumentation().getTargetContext();
        signal = new CountDownLatch(1);
    }

    private OperationObserver getObserver() {
        return new OperationObserver() {
            @Override
            public void hasFinished() {
                signal.countDown();
            }
        };
    }

    public void testDeleteEntity() throws Throwable {
        NoSQLEntity<SampleBean> entity = new NoSQLEntity<>("delete", "first");
        NoSQLEntity<SampleBean> entity2 = new NoSQLEntity<>("delete", "second");
        SampleBean bean1 = new SampleBean();
        SampleBean bean2 = new SampleBean();
        bean1.setId(1);
        bean2.setId(2);
        entity.setData(bean1);
        entity2.setData(bean2);

        final List<NoSQLEntity<SampleBean>> entities = new ArrayList<>(2);
        entities.add(entity);
        entities.add(entity2);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .addObserver(getObserver())
                        .save(entities);
            }
        });

        signal.await(2, TimeUnit.SECONDS);

        signal = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .bucketId("delete")
                        .entityId("first")
                        .addObserver(getObserver())
                        .delete();
            }
        });
        signal.await(2, TimeUnit.SECONDS);

        SimpleNoSQLDBHelper sqldbHelper = new SimpleNoSQLDBHelper(getInstrumentation().getTargetContext(), serialization, serialization);
        SQLiteDatabase db = sqldbHelper.getReadableDatabase();
        String[] columns = {SimpleNoSQLContract.EntityEntry.COLUMN_NAME_BUCKET_ID,
                SimpleNoSQLContract.EntityEntry.COLUMN_NAME_ENTITY_ID, SimpleNoSQLContract.EntityEntry.COLUMN_NAME_DATA};
        String[] selectionArgs = {"delete"};
        Cursor cursor = db.query(SimpleNoSQLContract.EntityEntry.TABLE_NAME, columns,
                SimpleNoSQLContract.EntityEntry.COLUMN_NAME_BUCKET_ID + "=?",
                selectionArgs, null, null, null);
        assertNotNull(cursor);

        assertEquals(1, cursor.getCount());
    }

    public void testDeleteBucket() throws Throwable {
        final List<NoSQLEntity<SampleBean>> lots = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            NoSQLEntity<SampleBean> entity = new NoSQLEntity<>("delete", "id" + i);
            SampleBean bean = new SampleBean();
            bean.setId(i);
            bean.setExists(i % 2 == 0);
            entity.setData(bean);
            lots.add(entity);
        }

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .addObserver(getObserver())
                        .save(lots);
            }
        });

        signal.await(2, TimeUnit.SECONDS);
        signal = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoSQL.with(context).using(SampleBean.class)
                        .bucketId("delete")
                        .addObserver(getObserver())
                        .delete();
            }
        });
        signal.await(2, TimeUnit.SECONDS);

        SimpleNoSQLDBHelper sqldbHelper = new SimpleNoSQLDBHelper(getInstrumentation().getTargetContext(), serialization, serialization);
        SQLiteDatabase db = sqldbHelper.getReadableDatabase();
        String[] columns = {SimpleNoSQLContract.EntityEntry.COLUMN_NAME_BUCKET_ID,
                SimpleNoSQLContract.EntityEntry.COLUMN_NAME_ENTITY_ID, SimpleNoSQLContract.EntityEntry.COLUMN_NAME_DATA};
        String[] selectionArgs = {"delete"};
        Cursor cursor = db.query(SimpleNoSQLContract.EntityEntry.TABLE_NAME, columns,
                SimpleNoSQLContract.EntityEntry.COLUMN_NAME_BUCKET_ID + "=?",
                selectionArgs, null, null, null);
        assertNotNull(cursor);

        assertEquals(0, cursor.getCount());
    }
}
