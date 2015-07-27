package com.rohit.sampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.colintmiller.simplenosql.DataFilter;
import com.colintmiller.simplenosql.GsonSerialization;
import com.colintmiller.simplenosql.NoSQL;
import com.colintmiller.simplenosql.NoSQLEntity;
import com.colintmiller.simplenosql.RetrievalCallback;
import com.uttarainfo.sampleapp.R;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private TextView tv;
    private TextView tv1;
    private String str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView);
        tv1 = (TextView) findViewById(R.id.textView1);
    }

    public void insert(View view){
        GsonSerialization serialization = new GsonSerialization();
        // NoSQL.with(this).start();
        // NoSQL.with(this).using(TestBean.class).bucketId("bucket").delete();
        List<NoSQLEntity<TestBean>> list = new ArrayList<>();
        Timer.start();
        NoSQLEntity<TestBean> entity = new NoSQLEntity<TestBean>("bucket","test");
        TestBean data = new TestBean();
        data.setName("Andrew");
        data.setEmail("andy@amail.com");
        data.setAddress("Austin town");
        entity.setData(data);
        list.add(entity);
        String jsonData  = serialization.serialize(entity.getData());
        Log.d(TAG,jsonData);
        NoSQLEntity<TestBean> entity1 = new NoSQLEntity<TestBean>("bucket","test1");
        TestBean data1 = new TestBean();
        data1.setName("Sheldon");
        data1.setEmail("shel@gmal.com");
        data1.setAddress("Goa");
        entity1.setData(data1);
        list.add(entity1);
        String jsonData1  = serialization.serialize(entity1.getData());
        Log.d(TAG, jsonData1);
        NoSQLEntity<TestBean> entity2 = new NoSQLEntity<TestBean>("bucket","test2");
        TestBean data2 = new TestBean();
        data2.setName("Anand");
        data2.setEmail("andy@yahoo.com");
        data2.setAddress("Bangalore");
        entity2.setData(data2);
        list.add(entity2);
        String jsonData2  = serialization.serialize(entity2.getData());
        Log.d(TAG, jsonData2);
        NoSQLEntity<TestBean> entity3 = new NoSQLEntity<TestBean>("bucket","test4");
        TestBean data3 = new TestBean();
        data3.setName("Angelina");
        data3.setEmail("angy@yahoo.com");
        data3.setAddress("New york");
        entity3.setData(data3);
        String jsonData3  = serialization.serialize(entity3.getData());
        Log.d(TAG, jsonData3);
        list.add(entity3);

        NoSQLEntity<TestBean> entity4 = new NoSQLEntity<TestBean>("bucket","test4");
        TestBean data4 = new TestBean();
        data4.setName("Smith");
        data4.setEmail("smi@yahoo.com");
        data4.setAddress("Lancaster");
        entity4.setData(data4);
        list.add(entity4);
        String jsonData4  = serialization.serialize(entity4.getData());
        Log.d(TAG, jsonData4);
        NoSQLEntity<TestBean> entity5 = new NoSQLEntity<TestBean>("bucket","test5");
        TestBean data5 = new TestBean();
        data5.setName("Murdock");
        data5.setEmail(" murray@yahoo.com");
        data5.setAddress("Maryland");
        entity5.setData(data5);
        list.add(entity5);
        String jsonData5  = serialization.serialize(entity5.getData());
        Log.d(TAG, jsonData5);
        NoSQL.with(this).using(TestBean.class).save(list);
        Timer.end();
        Log.d(TAG,"Execution time = "+Timer.getTotalTime());
        // NoSQL.with(this).stop();
        str = "Execution write time = "+Timer.getTotalTime();
        tv.setText(str);

    }

    public void retrieve(View view) {
        final List<TestBean> tbList = new ArrayList<>();
        // NoSQL.with(this).start();
        Timer.reset();
        Timer.start();
        NoSQL.with(this).using(TestBean.class).bucketId("bucket").retrieve(new RetrievalCallback<TestBean>() {
            @Override
            public void retrievedResults(List<NoSQLEntity<TestBean>> noSQLEntities) {
                Log.d(TAG, "entity size = " + noSQLEntities.size());
                if (noSQLEntities.size() > 0) {
                    for (NoSQLEntity<TestBean> noSQLEntity : noSQLEntities) {
                        tbList.add(noSQLEntity.getData());
                        tv1.setText("" + tbList);
                        Timer.end();
                        tv.setText(str + "\nExecution read time = " + Timer.getTotalTime());
                    }
                } else {
                    tv1.setText("No data");
                }
            }
        });
    }
    public void delete(View v){
        NoSQL.with(this).using(TestBean.class).bucketId("bucket").delete();
        tv1.setText("No data");
    }
    public void deleteSingleEntity(View view){
        NoSQL.with(this).using(TestBean.class).bucketId("bucket").entityId("test").delete();
        retrieve(view);

    }
    public void filterData(View v) {
        Timer.reset();
        Timer.start();
        final List<TestBean> tbList = new ArrayList<>();
        NoSQL.with(this).using(TestBean.class).bucketId("bucket").filter(new DataFilter<TestBean>() {
            @Override
            public boolean isIncluded(NoSQLEntity<TestBean> item) {
                TestBean tb = item.getData();
                return tb.getEmail().equals("andy@yahoo.com");
            }
        }).retrieve(new RetrievalCallback<TestBean>() {
            @Override
            public void retrievedResults(List<NoSQLEntity<TestBean>> noSQLEntities) {
                Log.d(TAG, "entity size = " + noSQLEntities.size());
                if (noSQLEntities.size() > 0) {
                    for (NoSQLEntity<TestBean> noSQLEntity : noSQLEntities) {
                        tbList.add(noSQLEntity.getData());
                        tv1.setText("" + tbList);
                        Timer.end();
                        tv.setText(str + "\nExecution read time = " + Timer.getTotalTime());
                    }
                } else {
                    tv1.setText("No data");
                }
            }

        });
    }
}
