package com.nu.art.cyborg.logcat;

import android.content.Intent;
import android.net.Uri;

import com.nu.art.cyborg.core.CyborgActivity;
import com.nu.art.cyborg.core.CyborgActivityBridgeImpl;
import com.nu.art.cyborg.logcat.sources.Logcat_ContentArchiveFile;

import java.io.File;

/**
 * Created by TacB0sS on 22/03/2018.
 */

public class Activity_ProxyZip
	extends CyborgActivity {

	@Override
	protected void onCreateImpl() {
		Uri uri = getIntent().getData();
		if (uri == null) {
			getModule(Module_LogcatViewer.class).toastDebug("Nothing to import");
			finish();
			return;
		}

		Intent intent = CyborgActivityBridgeImpl.composeIntent("", R.layout.activity__logcat_main);
		String uriAsString = uri.toString();
		uriAsString = uriAsString.substring(uriAsString.lastIndexOf("/") + 1);

		File tempFile = new File(getApplicationContext().getCacheDir(), uriAsString + "-temp.txt");

		Logcat_ContentArchiveFile source = new Logcat_ContentArchiveFile(cyborg, uri, tempFile);
		getModule(Module_LogcatViewer.class).addLogcatSource(source);
		getModule(Module_LogcatViewer.class).setActiveSourceAndRead(source);
		startActivity(intent);
		finish();
	}
}
