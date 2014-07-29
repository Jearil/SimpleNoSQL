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
    public NoSQLDeleteTaskTest() {
        super(Activity.class);
    }

    public void testDeleteEntity() {
        NoSQLEntity entity = new NoSQLEntity("delete", "first");
        NoSQLEntity entity2 = new NoSQLEntity("delete", "second");
        entity.setValue("id", 1);
        entity2.setValue("id", 2);

        NoSQLSaveTask saveTask = new NoSQLSaveTask(getInstrumentation().getTargetContext());
        saveTask.doInBackground(entity, entity2);

        NoSQLDeleteTask deleteTask = new NoSQLDeleteTask(getInstrumentation().getTargetContext());
        deleteTask.doInBackground("delete", "first");

        SimpleNoSQLDBHelper sqldbHelper = new SimpleNoSQLDBHelper(getInstrumentation().getTargetContext());
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
        List<NoSQLEntity> lots = new ArrayList<NoSQLEntity>(10);
        for (int i = 0; i < 10; i++) {
            NoSQLEntity entity = new NoSQLEntity("delete", "id" + i);
            entity.setValue("id", i);
            entity.setValue("even", i % 2 == 0);
            lots.add(entity);
        }

        NoSQLSaveTask saveTask = new NoSQLSaveTask(getInstrumentation().getTargetContext());
        saveTask.doInBackground(lots.toArray(new NoSQLEntity[0]));

        NoSQLDeleteTask deleteTask = new NoSQLDeleteTask(getInstrumentation().getTargetContext());
        deleteTask.doInBackground("delete");

        SimpleNoSQLDBHelper sqldbHelper = new SimpleNoSQLDBHelper(getInstrumentation().getTargetContext());
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
