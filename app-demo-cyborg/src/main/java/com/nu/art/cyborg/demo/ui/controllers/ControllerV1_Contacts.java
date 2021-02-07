

package com.nu.art.cyborg.demo.ui.controllers;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.widget.EditText;
import android.widget.TextView;

import com.nu.art.core.interfaces.Getter;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgAdapter;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgRecycler;
import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.core.dataModels.CursorDataModel;
import com.nu.art.cyborg.core.dataModels.QueryBuilder;
import com.nu.art.cyborg.core.dataModels.QueryException;
import com.nu.art.cyborg.core.modules.ThreadsModule;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.ContactsModule;
import com.nu.art.cyborg.modules.PermissionModule;
import com.nu.art.cyborg.modules.PermissionModule.PermissionResultListener;
import com.nu.art.cyborg.modules.calls.NativeCallsModule.NativeCallsListener;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

@ReflectiveInitialization
public class ControllerV1_Contacts
	extends CyborgController
	implements PermissionResultListener, NativeCallsListener {

	@ViewIdentifier(viewId = R.id.RV_Contacts)
	private CyborgRecycler recycler;

	@ViewIdentifier(viewId = R.id.ET_Search,
	                listeners = ViewListener.OnTextChangedListener)
	private EditText search;

	private ContactsModule contactsModule;
	private ContactsResolver resolver;
	private String query = "";

	private ControllerV1_Contacts() {
		super(R.layout.controller__contacts);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate() {
		CyborgAdapter<Contact> contactsAdapter = new CyborgAdapter<>(this, Renderer_Contact.class);
		resolver = new ContactsResolver();
		contactsAdapter.setResolver(resolver);
		recycler.setAdapter(contactsAdapter);

		resolver.model.refresh();
	}

	@Override
	public void onResume() {
		super.onResume();
		getModule(PermissionModule.class).requestPermission(100, permission.READ_CONTACTS);
	}

	@Override
	public void onPermissionsRejected(int requestCode, String[] rejected) {
		postOnUI(new Runnable() {
			@Override
			public void run() {
				getActivity().onBackPressed();
			}
		});
	}

	@Override
	public void onTextChanged(TextView view, CharSequence string, int start, int before, int count) {
		query = search.getText().toString();
		recycler.invalidateDataModel();
	}

	@Override
	@SuppressLint("MissingPermission")
	public void onAllPermissionsGranted(int requestCode) {
		logInfo("permissions granted");
		recycler.invalidateDataModel();
	}

	@Override
	public void onCallsStateChanged() {
		//		recycler.invalidateDataModel();
	}

	private class Contact {

		String name;
		String phone;

		public Contact(Cursor cursor) {
			name = cursor.getString(0);
			phone = cursor.getString(2);
		}

		@Override
		public String toString() {
			return name + " " + phone;
		}
	}

	private static class Renderer_Contact
		extends ItemRenderer<Contact> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		public Renderer_Contact() {
			super(R.layout.renderer__example_double);
		}

		@Override
		protected void renderItem(Contact item) {
			exampleLabel.setText(item.toString());
		}
	}

	private class ContactsResolver
		implements Getter<CursorDataModel<Contact>> {

		Handler bgHandler = getModule(ThreadsModule.class).getDefaultHandler("contacts-loading");
		CursorDataModel<Contact> model = new CursorDataModel<Contact>(ControllerV1_Contacts.this, bgHandler, Contact.class) {

			@Override
			protected Contact convert(Cursor cursor) {
				return new Contact(cursor);
			}

			@Override
			protected Cursor getCursor() {
				String[] projection = {
					Contacts.DISPLAY_NAME,
					Contacts._ID,
					Phone.NUMBER,
					Phone.IS_PRIMARY,
					Phone.IS_SUPER_PRIMARY,
					Contacts.PHOTO_URI,
					Contacts.PHOTO_THUMBNAIL_URI,
					Contacts.STARRED,
					Phone.CONTACT_ID
				};

				String regexpQuery = contactsModule.convertToNumericRegexp(query);
				Uri uri = regexpQuery.length() == 0 ? Phone.CONTENT_URI : Uri.withAppendedPath(Phone.CONTENT_FILTER_URI, Uri.encode(query));

				QueryBuilder builder = new QueryBuilder().setSelectColumnsClause(projection).setUri(uri).setOrderByClause("LOWER(" + Contacts.DISPLAY_NAME + ")");

				if (regexpQuery.length() != 0) {
					builder.setWhereClauseArgs(regexpQuery).setWhereClause(Phone.NUMBER + " REGEXP ?");
				}

				try {
					return builder.execute(getContentResolver());
				} catch (QueryException e) {
					e.printStackTrace();
				}
				return null;
			}
		};

		@Override
		public CursorDataModel<Contact> get() {
			return model;
		}
	}
}