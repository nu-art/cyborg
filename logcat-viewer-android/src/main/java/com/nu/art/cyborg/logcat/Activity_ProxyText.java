package com.nu.art.cyborg.logcat;

import android.content.Intent;
import android.net.Uri;

import com.nu.art.cyborg.core.CyborgActivity;
import com.nu.art.cyborg.core.CyborgActivityBridgeImpl;
import com.nu.art.cyborg.logcat.sources.Logcat_ContentFile;

/**
 * Created by TacB0sS on 22/03/2018.
 */

public class Activity_ProxyText
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
		Logcat_ContentFile source = new Logcat_ContentFile(cyborg, uri);
		getModule(Module_LogcatViewer.class).addLogcatSource(source);
		getModule(Module_LogcatViewer.class).setActiveSourceAndRead(source);
		startActivity(intent);
		finish();
	}
}
