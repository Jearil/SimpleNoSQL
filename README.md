SimpleNoSQL
===========

A simple NoSQL client for Android. If you ever wanted to just save some data but didn't really want to worry about
where it was going to be stored and didn't want to go through the hassel of setting up a database manually, this
library can really speed things up. Saving data is pretty easy:

    NoSQLEntity<SampleBean> entity = new NoSQLEntity<SampleBean>("bucket", "entityId");
	SampleBean data = new SampleBean();
	data.setName("Colin");
	Map<String, Integer> birthday = new HashMap<String, Integer>();
	birthday.set("day", "17");
	birthday.set("month", "02");
	birthday.set("year", "1982");
	data.setBirthdayMap(birthday);
	entity.setData(data);

	NoSQL noSQL = new NoSQL(context);
	noSQL.save(entity);

Later, when you want to retrieve it, you can use a callback interface and the bucket and ID you saved with:

    NoSQL noSQL = new NoSQL(context);
	RetrievalCallback<SampleBean> callback = new RetrievalCallback<SampleBean>() {
		public void retrieveResults(List<NoSQLEntity<SampleBean> entities) {
			// Display results or something	
			SampleBean firstBean = entities.get(0).getData(); // always check length of a list first...
		}	
	};
    noSQL.getEntity("bucket", "entityId", callback, SampleBean.class);

## Development
This project is still very new and under active development. The API is in a wildly fluctuating state as I figure out
the best way of making it simple to access documents. The current API uses gson for serialization and deserialization.
You can access snapshots on sonatype by adding the following to your modules build.gradle:

    repositories {
	    mavenCentral()
	    maven {
		        url 'https://oss.sonatype.org/content/repositories/snapshots'
		    }
	}

    dependencies {
	    compile 'com.colintmiller:simplenosql:0.1.3-SNAPSHOT'
	}
