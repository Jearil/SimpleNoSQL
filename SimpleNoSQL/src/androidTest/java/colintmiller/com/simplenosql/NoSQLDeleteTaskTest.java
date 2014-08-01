package colintmiller.com.simplenosql;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityUnitTestCase;
import colintmiller.com.simplenosql.db.SimpleNoSQLContract;
import colintmiller.com.simplenosql.db.SimpleNoSQLDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests to verify the deletion asyncTask performs as expected
 */
public class NoSQLDeleteTaskTest extends ActivityUnitTestCase {
    private GsonSerialization serialization;

    public NoSQLDeleteTaskTest() {
        super(Activity.class);
        serialization = new GsonSerialization();
    }

    public void testDeleteEntity() {
        NoSQLEntity<SampleBean> entity = new NoSQLEntity<SampleBean>("delete", "first");
        NoSQLEntity<SampleBean> entity2 = new NoSQLEntity<SampleBean>("delete", "second");
        SampleBean bean1 = new SampleBean();
        SampleBean bean2 = new SampleBean();
        bean1.setId(1);
        bean2.setId(2);
        entity.setData(bean1);
        entity2.setData(bean2);

        NoSQLSaveTask saveTask = new NoSQLSaveTask(getInstrumentation().getTargetContext(), serialization);
        saveTask.doInBackground(entity, entity2);

        NoSQLDeleteTask deleteTask = new NoSQLDeleteTask(getInstrumentation().getTargetContext());
        deleteTask.doInBackground("delete", "first");

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

    public void testDeleteBucket() {
        List<NoSQLEntity<SampleBean>> lots = new ArrayList<NoSQLEntity<SampleBean>>(10);
        for (int i = 0; i < 10; i++) {
            NoSQLEntity<SampleBean> entity = new NoSQLEntity<SampleBean>("delete", "id" + i);
            SampleBean bean = new SampleBean();
            bean.setId(i);
            bean.setExists(i % 2 == 0);
            entity.setData(bean);
            lots.add(entity);
        }

        NoSQLSaveTask saveTask = new NoSQLSaveTask(getInstrumentation().getTargetContext(), serialization);
        saveTask.doInBackground(lots.toArray(new NoSQLEntity[0]));

        NoSQLDeleteTask deleteTask = new NoSQLDeleteTask(getInstrumentation().getTargetContext());
        deleteTask.doInBackground("delete");

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
