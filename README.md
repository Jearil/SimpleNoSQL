SimpleNoSQL
===========

A simple NoSQL client for Android. If you ever wanted to just save some data but didn't really want to worry about
where it was going to be stored and didn't want to go through the hassel of setting up a database manually, this
library can really speed things up. Saving data is pretty easy:

    NoSQLEntity entity = new NoSQLEntity("bucket", "entityId");
	entity.setValue("name", "Colin");
	Map<String, Integer> birthday = new HashMap<String, Integer>();
	birthday.set("day", "17");
	birthday.set("month", "02");
	birthday.set("year", "1982");
	entity.setValue("birthday", birthday);

	NoSQL noSQL = new NoSQL(context);
	noSQL.save(entity);

Later, when you want to retrieve it, you can use a callback interface and the bucket and ID you saved with:

    NoSQL noSQL = new NoSQL(context);
	RetrievalCallback callback = new RetrievalCallback() {
		public void retrieveResults(List<NoSQLEntity entities) {
			// Display results or something	
		}	
	};
    noSQL.getEntity("bucket", "entityId", callback);

## Development
This project is still very new and under active development. There are plans for speeding up the creation of entities
and possibly doing direct mappings of Objects to Entities. The API is partly based on a simplified version of the
currently unreleased Google Cloud Save API.
