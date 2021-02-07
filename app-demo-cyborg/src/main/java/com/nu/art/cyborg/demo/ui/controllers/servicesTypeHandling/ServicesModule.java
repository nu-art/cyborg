

package com.nu.art.cyborg.demo.ui.controllers.servicesTypeHandling;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;

import com.nu.art.core.tools.ArrayTools;
import com.nu.art.cyborg.annotations.ModuleDescriptor;
import com.nu.art.cyborg.common.utils.GenericServiceConnection;
import com.nu.art.cyborg.common.utils.GenericServiceConnection.ServiceConnectionListener;
import com.nu.art.cyborg.core.CyborgModule;
import com.nu.art.cyborg.demo.R;

import java.util.HashMap;

/**
 * Created by TacB0sS on 18-May 2016.
 */
@ModuleDescriptor
public class ServicesModule
	extends CyborgModule {

	public enum ServicesTypes {
		Sticky(ServiceV1_Sticky.class, R.string.ServiceName_Sticky),
		Foreground(ServiceV1_Foreground.class, R.string.ServiceName_Foreground);

		public final Class<? extends Service> serviceType;

		public final int labelId;

		ServicesTypes(Class<? extends Service> stickyServiceClass, int labelId) {
			this.serviceType = stickyServiceClass;
			this.labelId = labelId;
		}
	}

	private void onServiceBindedConfirmed(Class<? extends Service> serviceType, GenericServiceConnection<? extends Service> connection) {
		ServiceConnection[] serviceConnections = getServiceConnections(serviceType);
		serviceConnections = ArrayTools.appendElement(serviceConnections, connection);
		putServiceConnections(serviceType, serviceConnections);
	}

	private ServiceConnection[] putServiceConnections(Class<? extends Service> serviceType, ServiceConnection[] serviceConnections) {
		return connections.put(serviceType, serviceConnections);
	}

	private void removeServiceConnection(Class<? extends Service> serviceType, ServiceConnection connection) {
		ServiceConnection[] serviceConnections = getServiceConnections(serviceType);
		serviceConnections = ArrayTools.removeElement(serviceConnections, connection);
		putServiceConnections(serviceType, serviceConnections);
	}

	public final ServiceConnection[] getServiceConnections(Class<? extends Service> serviceType) {
		ServiceConnection[] serviceConnections = connections.get(serviceType);
		if (serviceConnections == null)
			return new ServiceConnection[0];

		return serviceConnections;
	}

	private HashMap<Class<? extends Service>, ServiceConnection[]> connections = new HashMap<>();

	private HashMap<Class<? extends Service>, ServiceDetails> services = new HashMap<>();

	@Override
	protected void init() {
		ActivityManager manager = getSystemService(ActivityService);
		for (ServicesTypes _serviceType : ServicesTypes.values()) {
			Class<? extends Service> serviceType = _serviceType.serviceType;
			ServiceDetails serviceDetails = new ServiceDetails(serviceType);
			services.put(serviceType, serviceDetails);

			for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if (!serviceType.getName().equals(service.service.getClassName()))
					continue;
			}
		}
	}

	public String getServiceTypeInfo(Class<? extends Service> serviceType) {
		return services.get(serviceType).toString() + "\nConnections: " + getServiceConnections(serviceType).length;
	}

	public void onServiceCreated(ReportingService service) {
		ServiceDetails details = services.get(service.getClass());
		details.created++;
	}

	public void onServiceDestroyed(ReportingService service) {
		ServiceDetails details = services.get(service.getClass());
		details.created--;
	}

	public void onServiceBinded(ReportingService service) {
		ServiceDetails details = services.get(service.getClass());
		details.binded++;
	}

	public void onServiceUnbind(ReportingService service) {
		ServiceDetails details = services.get(service.getClass());
		details.binded = 0;
	}

	public void startService(Activity activity, Class<? extends Service> serviceType) {
		Intent serviceIntent = new Intent(activity, serviceType);
		activity.startService(serviceIntent);
	}

	public void stopService(Activity activity, Class<? extends Service> serviceType) {
		Intent serviceIntent = new Intent(activity, serviceType);
		activity.stopService(serviceIntent);
	}

	<_ServiceType extends Service> void bindService(Activity activity, final Class<_ServiceType> serviceType, int flags) {
		final GenericServiceConnection<_ServiceType> serviceConnection = new GenericServiceConnection<>(serviceType);
		serviceConnection.addListener(new ServiceConnectionListener<_ServiceType>() {
			@Override
			public void onServiceConnected(_ServiceType service) {
				onServiceBindedConfirmed(service.getClass(), serviceConnection);
				logDebug("Service connected, " + serviceType + ": " + service.toString().split("@")[1]);
			}

			@Override
			public void onServiceDisconnected(_ServiceType service) {
				logDebug("Service disconnected, " + serviceType + ": " + service.toString().split("@")[1]);
				removeServiceConnection(serviceType, serviceConnection);
			}
		});
		Intent serviceIntent = new Intent(activity, serviceType);
		activity.bindService(serviceIntent, serviceConnection, flags);//Context.BIND_AUTO_CREATE);
	}

	void unbindService(Activity activity, Class<? extends Service> serviceType, ServiceConnection connection) {
		activity.unbindService(connection);
		removeServiceConnection(serviceType, connection);
	}

	public void unbindAll(Activity activity) {
		ServiceConnection[] serviceConnections;
		for (Class<? extends Service> serviceType : connections.keySet()) {
			serviceConnections = connections.get(serviceType);
			for (ServiceConnection connection : serviceConnections) {
				unbindService(activity, serviceType, connection);
			}
		}
	}
}
