package com.colintmiller.simplenosql;

import android.content.Context;

import java.util.concurrent.CountDownLatch;

/**
 * Created by cmiller on 8/12/14.
 */
public class TestUtils {
    private TestUtils() {}

    public static CountDownLatch cleanBucket(String bucket, Context context) {
        final CountDownLatch signal = new CountDownLatch(1);

        NoSQL.with(context).using(TestUtils.class)
                .bucketId(bucket)
                .addObserver(new OperationObserver() {
                    @Override
                    public void hasFinished() {
                        signal.countDown();
                    }
                })
                .delete();
        return signal;
    }
}
