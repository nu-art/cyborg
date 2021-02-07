

package com.nu.art.cyborg.demo.ui.controllers.servicesTypeHandling;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Intent;

import com.nu.art.cyborg.core.CyborgActivityBridgeImpl;
import com.nu.art.cyborg.core.CyborgBuilder;
import com.nu.art.cyborg.core.CyborgBuilder.LaunchConfiguration;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;

/**
 * Created by TacB0sS on 16-May 2016.
 */
public class ServiceV1_Foreground
	extends ReportingService {

	public class LocalBinder
		extends BaseBinder<ServiceV1_Foreground> {

		@Override
		public ServiceV1_Foreground getService() {
			return ServiceV1_Foreground.this;
		}
	}

	@Override
	protected BaseBinder createBinder() {
		return new LocalBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		Builder builder = new Builder(this);
		builder.setContentText("Foreground Service");
		builder.setSmallIcon(R.drawable.arrow);

		LaunchConfiguration launchConfiguration = CyborgBuilder.getInstance().getLaunchConfiguration();
		Intent i = CyborgActivityBridgeImpl.composeIntent(launchConfiguration);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
		builder.setContentIntent(pi);
		Notification note = builder.getNotification();
		note.flags |= Notification.FLAG_NO_CLEAR;

		startForeground(CyborgController.getRandomShort(), note);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		//		Intent restartService = new Intent(getApplicationContext(), this.getClass());
		//		restartService.setPackage(getPackageName());
		//		PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
		//		AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		//		alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
		super.onDestroy();
	}
}
