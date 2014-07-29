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
 * Tests for saving entities to the DB. This includes saving a single entity or saving multiple entities.
 */
public class NoSQLSaveTaskTest extends ActivityUnitTestCase<Activity> {

    public NoSQLSaveTaskTest() {
        super(Activity.class);
    }

    public void testSaveEntity() {
        NoSQLEntity entity = new NoSQLEntity("test", "first");
        entity.setValue("name", "SimpleNoSQL");

        NoSQLSaveTask saveTask = new NoSQLSaveTask(getInstrumentation().getTargetContext());
        saveTask.doInBackground(entity);

        assertNotNull("Activity is null when it should not have been", getInstrumentation().getTargetContext());
        SimpleNoSQLDBHelper sqldbHelper = new SimpleNoSQLDBHelper(getInstrumentation().getTargetContext());
        SQLiteDatabase db = sqldbHelper.getReadableDatabase();
        String[] columns = {SimpleNoSQLContract.EntityEntry.COLUMN_NAME_BUCKET_ID,
                SimpleNoSQLContract.EntityEntry.COLUMN_NAME_ENTITY_ID, SimpleNoSQLContract.EntityEntry.COLUMN_NAME_DATA};
        String[] selectionArgs = {"test", "first"};
        Cursor cursor = db.query(SimpleNoSQLContract.EntityEntry.TABLE_NAME, columns,
                SimpleNoSQLContract.EntityEntry.COLUMN_NAME_BUCKET_ID + "=? and " + SimpleNoSQLContract.EntityEntry.COLUMN_NAME_ENTITY_ID + "=?",
                selectionArgs, null, null, null);
        assertNotNull(cursor);
        assertEquals(cursor.getCount(), 1);
    }

    public void testSaveEntities() {
        List<NoSQLEntity> allEntities = new ArrayList<NoSQLEntity>(3);
        for(int i = 0; i < 3; i++) {
            NoSQLEntity entity = new NoSQLEntity("sample", "entity" + i);
            entity.setValue("id", i);
            entity.setValue("even", i % 2 == 0);
            allEntities.add(entity);
        }

        NoSQLSaveTask saveTask = new NoSQLSaveTask(getInstrumentation().getTargetContext());
        saveTask.doInBackground(allEntities.toArray(new NoSQLEntity[0]));

        SimpleNoSQLDBHelper sqldbHelper = new SimpleNoSQLDBHelper(getInstrumentation().getTargetContext());
        SQLiteDatabase db = sqldbHelper.getReadableDatabase();
        String[] columns = {SimpleNoSQLContract.EntityEntry.COLUMN_NAME_BUCKET_ID,
                SimpleNoSQLContract.EntityEntry.COLUMN_NAME_ENTITY_ID, SimpleNoSQLContract.EntityEntry.COLUMN_NAME_DATA};
        String[] selectionArgs = {"sample"};
        Cursor cursor = db.query(SimpleNoSQLContract.EntityEntry.TABLE_NAME, columns,
                SimpleNoSQLContract.EntityEntry.COLUMN_NAME_BUCKET_ID + "=?",
                selectionArgs, null, null, null);
        assertNotNull(cursor);

        assertEquals(cursor.getCount(), 3);
    }

    // TODO: Write an "update" test (overrides an already saved entity)
}
