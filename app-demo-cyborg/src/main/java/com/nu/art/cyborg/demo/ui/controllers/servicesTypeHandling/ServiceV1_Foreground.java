
package com.nu.art.cyborg.demo.ui.controllers.servicesTypeHandling;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

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

	private static final String ChannelId = "cyborg.demo.fgs";

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

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(ChannelId, "Foreground demo", NotificationManager.IMPORTANCE_LOW);
			getSystemService(NotificationManager.class).createNotificationChannel(channel);
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ChannelId);
		builder.setContentText("Foreground Service");
		builder.setSmallIcon(R.drawable.arrow);

		LaunchConfiguration launchConfiguration = CyborgBuilder.getInstance().getLaunchConfiguration();
		Intent i = CyborgActivityBridgeImpl.composeIntent(launchConfiguration);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_IMMUTABLE);
		builder.setContentIntent(pi);
		Notification note = builder.build();
		note.flags |= Notification.FLAG_NO_CLEAR;

		startForegroundServiceNotification(CyborgController.getRandomShort(), note);
		return START_NOT_STICKY;
	}
}
