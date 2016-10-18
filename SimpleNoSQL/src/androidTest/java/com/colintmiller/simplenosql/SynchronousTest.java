package com.colintmiller.simplenosql;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.ActivityUnitTestCase;
import com.colintmiller.simplenosql.threading.QueryDelivery;
import com.colintmiller.simplenosql.toolbox.SynchronousRetrieval;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by cmiller on 10/21/14.
 */
public class SynchronousTest extends ActivityUnitTestCase<Activity> {

    private Context context;

    public SynchronousTest() {
        super(Activity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.context = getInstrumentation().getTargetContext();
    }

    public void testSynchronousGet() throws Throwable {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<SampleBean> result = new ArrayList<>();
        assertEquals(0, result.size());
        NoSQL.with(context).using(SampleBean.class).bucketId("dne").retrieve(new RetrievalCallback<SampleBean>() {
            @Override
            public void retrievedResults(List<NoSQLEntity<SampleBean>> noSQLEntities) {
                result.add(new SampleBean());
                latch.countDown();
            }
        });
        latch.await();
        assertEquals(1, result.size());
    }

    public void testSynchronousRetrieval() throws Throwable {
        SampleBean item = new SampleBean();
        item.setName("item");
        final CountDownLatch lock = new CountDownLatch(1);
        NoSQL.with(context).using(SampleBean.class).addObserver(new OperationObserver() {
            @Override
            public void hasFinished() {
                lock.countDown();
            }
        }).save(new NoSQLEntity<>("dne", "1", item));
        lock.await();
        SynchronousRetrieval<SampleBean> retrievalCallback = new SynchronousRetrieval<>();
        NoSQL.with(context).using(SampleBean.class).bucketId("dne").retrieve(retrievalCallback);
        List<NoSQLEntity<SampleBean>> results = retrievalCallback.getSynchronousResults();
        assertEquals(1, results.size());
    }

    /**
     * Honestly why anyone would ever want to block the UI thread to wait for their data is a mystery. But in case you
     * feel your foot is too healthy and you'd like to shoot it, here's how.
     *
     * @throws Throwable if something goes horribly wrong
     */
    public void testSynchronousUIRetrieval() throws Throwable {

        final CountDownLatch lock = new CountDownLatch(1);
        final List<NoSQLEntity<SampleBean>> results = new ArrayList<>();

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                final CountDownLatch saveLock = new CountDownLatch(1);
                SampleBean item = new SampleBean();
                item.setName("item");
                HandlerThread thread = new HandlerThread("NoSQLDelivery");
                thread.start();
                QueryDelivery delivery = new QueryDelivery(new Handler(thread.getLooper()));
                NoSQL.with(context, 1, delivery)
                        .using(SampleBean.class)
                        .addObserver(new OperationObserver() {
                            @Override
                            public void hasFinished() {
                                saveLock.countDown();
                            }
                        }).save(new NoSQLEntity<>("dne", "1", item));

                SynchronousRetrieval<SampleBean> retrievalCallback = new SynchronousRetrieval<>();
                saveLock.countDown();

                NoSQL.with(context, 1, delivery).using(SampleBean.class).bucketId("dne").retrieve(retrievalCallback);
                results.addAll(retrievalCallback.getSynchronousResults());

                lock.countDown();
            }
        });

        lock.await();
        assertEquals(1, results.size());
    }
}
