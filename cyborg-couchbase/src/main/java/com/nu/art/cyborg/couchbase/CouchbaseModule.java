package com.nu.art.cyborg.couchbase;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Database.ChangeEvent;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.replicator.Replication.ChangeListener;
import com.google.gson.Gson;
import com.nu.art.cyborg.core.CyborgModule;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by tacb0ss on 29/11/2017.
 */

public class CouchbaseModule
	extends CyborgModule {

	private ReplicationBuilder[] push;
	private ReplicationBuilder[] pull;
	private HashMap<String, Database> dbs = new HashMap<>();

	private Gson gson;
	private Manager manager;

	@Override
	protected void init() {
		gson = new Gson();
	}

	private Database getOrCreate(final String name)
		throws CouchbaseLiteException, IOException {
		Database db = dbs.get(name);
		if (db == null) {
			InternalListener internalListener = new InternalListener() {

				@Override
				public void databaseClosing() {
					logInfo("Database closed: " + name);
				}

				@Override
				public void changed(ChangeEvent event) {
					logInfo("Database changed: " + name + ", event: " + event);
				}
			};

			dbs.put(name, db = getOrCreateManager().getDatabase(name));
			db.addChangeListener(internalListener);
			db.addDatabaseListener(internalListener);
		}

		return db;
	}

	private Manager getOrCreateManager()
		throws IOException {
		if (manager == null)
			manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);

		return manager;
	}

	interface InternalListener
		extends Database.ChangeListener, Database.DatabaseListener {

	}

	public class ReplicationBuilder {

		private Replication replicate;
		private String url;
		private String dbName;
		private String[] channels;
		private ChangeListener changeListener;
		private Authenticator auth;

		public ReplicationBuilder setUrl(String url) {
			this.url = url;
			return this;
		}

		public ReplicationBuilder setChannels(String... channels) {
			this.channels = channels;
			return this;
		}

		public ReplicationBuilder setAuth(Authenticator auth) {
			this.auth = auth;
			return this;
		}

		public ReplicationBuilder setChangeListener(ChangeListener changeListener) {
			this.changeListener = changeListener;
			return this;
		}

		public ReplicationBuilder setDbName(String dbName) {
			this.dbName = dbName;
			return this;
		}

		public final void buildPush()
			throws CouchbaseLiteException, IOException {
			build(getOrCreate(dbName).createPushReplication(new URL(url)));
		}

		public final void buildPull()
			throws CouchbaseLiteException, IOException {
			build(getOrCreate(dbName).createPullReplication(new URL(url)));
		}

		private void build(Replication replicate) {
			this.replicate = replicate;

			replicate.setAuthenticator(auth);
			replicate.setChannels(Arrays.asList(channels));
			replicate.setContinuous(true);
			replicate.addChangeListener(changeListener);
			replicate.setCreateTarget(true);
			replicate.start();
		}
	}
}
