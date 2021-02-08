/*
 * The cyborg-firebase module, meant to provide a simpler API to Firebase
 * and enforce some good conventions to help you out...
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.cyborg.firebase;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.CompletionListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.nu.art.core.generics.Processor;
import com.nu.art.cyborg.annotations.ModuleDescriptor;
import com.nu.art.cyborg.core.CyborgBuilder;
import com.nu.art.cyborg.core.CyborgModule;
import com.nu.art.storage.StringPreference;

import java.util.HashMap;

@SuppressWarnings("unused")
@ModuleDescriptor
public class FirebaseModule
	extends CyborgModule {

	private final Gson gson = new Gson();

	private StringPreference token = new StringPreference("firebase-token", null);

	@SuppressWarnings("WeakerAccess")
	public static class FirebaseKeyDB<Value> {

		final String dbName;

		final String pathToResource;

		final Class<Value> valueClass;

		private boolean listening;

		public FirebaseKeyDB(String dbName, String pathToResource, Class<Value> valueClass) {
			this.dbName = dbName;
			this.pathToResource = pathToResource;
			this.valueClass = valueClass;
		}

		public void setListening(boolean listening) {
			this.listening = listening;
		}

		boolean isListening() {
			return listening;
		}

		public final String composeUrl() {
			return "https://" + dbName + ".firebaseio.com/" + pathToResource;
		}

		@Override
		public String toString() {
			return composeUrl() + " <> " + valueClass.getSimpleName();
		}
	}

	public interface OnValueReceivedListener<Value> {

		void onResponse(Value value);

		void onError(FirebaseException error);
	}

	public interface OnValueUpdatedListener {

		void onCompleted(FirebaseKeyDB key);

		void onError(FirebaseException error);
	}

	@Override
	protected void init() {
		Firebase.setAndroidContext(getApplicationContext());
	}

	public synchronized <Value> void monitorTree(final FirebaseKeyDB<Value> key, final OnValueReceivedListener<Value> listener) {
		if (key.isListening())
			return;

		String url = key.composeUrl();
		Firebase firebase = new Firebase(url);
		logDebug("Getting value from firebase: " + url);
		key.setListening(true);

		firebase.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				logDebug("Got value for key: " + key);
				if (!dataSnapshot.exists()) {
					listener.onResponse(null);
					return;
				}

				try {
					listener.onResponse(convertValueWithGson(dataSnapshot, key));
				} catch (RuntimeException e) {
					logError("Error extracting value for key: " + key, e);
					throw e;
				}
			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {
				logError("Error: ", firebaseError.toException());
				listener.onError(firebaseError.toException());
			}
		});
	}

	private <Value> Value convertValueWithGson(DataSnapshot dataSnapshot, FirebaseKeyDB<Value> key) {
		return gson.fromJson(gson.toJson(dataSnapshot.getValue()), key.valueClass);
	}

	public <Value> void getValueOneshot(final FirebaseKeyDB<Value> key, final OnValueReceivedListener<Value> listener) {
		if (key.isListening())
			return;

		String url = key.composeUrl();
		Firebase firebase = new Firebase(url);
		key.setListening(true);

		logDebug("Getting value from firebase: " + url);
		firebase.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				logDebug("Got value for key: " + key);
				key.setListening(false);

				if (!dataSnapshot.exists()) {
					listener.onResponse(null);
					return;
				}

				try {
					listener.onResponse(convertValueWithGson(dataSnapshot, key));
				} catch (RuntimeException e) {
					logError("Error extracting value for key: " + key, e);
					throw e;
				}
			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {
				key.setListening(false);
				logError("Error: ", firebaseError.toException());
				listener.onError(firebaseError.toException());
			}
		});
	}

	public <Value> void updateValue(final FirebaseKeyDB<Value> key, Value instance, final OnValueUpdatedListener listener) {
		String url = key.composeUrl();
		Firebase firebase = new Firebase(url);
		firebase.setValue(gson.fromJson(gson.toJson(instance), HashMap.class), new CompletionListener() {
			@Override
			public void onComplete(FirebaseError firebaseError, Firebase firebase) {
				if (firebaseError != null) {
					listener.onError(firebaseError.toException());
					return;
				}

				listener.onCompleted(key);
			}
		});
	}

	@Override
	protected void printDetails() {
		logInfo("Registration token: " + token.get());
	}

	private void dispatchFirebaseMessageReceived(final RemoteMessage message) {
		dispatchModuleEvent("Dispatched firebase message", FirebaseNotificationListener.class, new Processor<FirebaseNotificationListener>() {
			@Override
			public void process(FirebaseNotificationListener listener) {
				listener.onPushMessageReceived(message);
			}
		});
	}

	private void onTokenUpdated(String token) {
		this.token.set(token);
		dispatchModuleEvent("Firebase token updated", FirebaseTokenListener.class, new Processor<FirebaseTokenListener>() {
			@Override
			public void process(FirebaseTokenListener listener) {
				listener.onFirebaseTokenUpdated();
			}
		});
	}

	public String getFirebaseToken() { return token.get(); }

	public interface FirebaseNotificationListener {

		void onPushMessageReceived(RemoteMessage message);
	}

	public interface FirebaseTokenListener {

		void onFirebaseTokenUpdated();
	}

	public static class StupidService
		extends com.google.firebase.messaging.FirebaseMessagingService {

		@Override
		public void onMessageReceived(RemoteMessage remoteMessage) {
			CyborgBuilder.getInstance().getModule(FirebaseModule.class).dispatchFirebaseMessageReceived(remoteMessage);
		}
	}

	public static class AnotherStupidService
		extends com.google.firebase.iid.FirebaseInstanceIdService {

		@Override
		public void onTokenRefresh() {
			CyborgBuilder.getInstance().getModule(FirebaseModule.class).onTokenUpdated(FirebaseInstanceId.getInstance().getToken());
		}
	}
}


