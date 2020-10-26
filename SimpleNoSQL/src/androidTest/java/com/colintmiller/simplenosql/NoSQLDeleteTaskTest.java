package com.colintmiller.simplenosql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.colintmiller.simplenosql.db.SimpleNoSQLContract;
import com.colintmiller.simplenosql.db.SimpleNoSQLDBHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests to verify the deletion asyncTask performs as expected
 */
@RunWith(AndroidJUnit4.class)
public class NoSQLDeleteTaskTest {
    private GsonSerialization serialization;
    private Context context;
    private CountDownLatch signal;

    public NoSQLDeleteTaskTest() {
        serialization = new GsonSerialization();
    }

    @Before
    public void setUp() throws Exception {
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

    @Test
    public void testDeleteEntity() throws Throwable {
        NoSQLEntity<SampleBean> entity = new NoSQLEntity<SampleBean>("delete", "first");
        NoSQLEntity<SampleBean> entity2 = new NoSQLEntity<SampleBean>("delete", "second");
        SampleBean bean1 = new SampleBean();
        SampleBean bean2 = new SampleBean();
        bean1.setId(1);
        bean2.setId(2);
        entity.setData(bean1);
        entity2.setData(bean2);

        final List<NoSQLEntity<SampleBean>> entities = new ArrayList<>(2);
        entities.add(entity);
        entities.add(entity2);

        // setup entities
        NoSQL.with(context).using(SampleBean.class)
                .addObserver(getObserver())
                .save(entities);

        signal.await(2, TimeUnit.SECONDS);

        signal = new CountDownLatch(1);

        // call delete
        NoSQL.with(context).using(SampleBean.class)
                .bucketId("delete")
                .entityId("first")
                .addObserver(getObserver())
                .delete();

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

    @Test
    public void testDeleteBucket() throws Throwable {
        final List<NoSQLEntity<SampleBean>> lots = new ArrayList<NoSQLEntity<SampleBean>>(10);
        for (int i = 0; i < 10; i++) {
            NoSQLEntity<SampleBean> entity = new NoSQLEntity<SampleBean>("delete", "id" + i);
            SampleBean bean = new SampleBean();
            bean.setId(i);
            bean.setExists(i % 2 == 0);
            entity.setData(bean);
            lots.add(entity);
        }

                NoSQL.with(context).using(SampleBean.class)
                        .addObserver(getObserver())
                        .save(lots);

        signal.await(2, TimeUnit.SECONDS);
        signal = new CountDownLatch(1);


                NoSQL.with(context).using(SampleBean.class)
                        .bucketId("delete")
                        .addObserver(getObserver())
                        .delete();

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
