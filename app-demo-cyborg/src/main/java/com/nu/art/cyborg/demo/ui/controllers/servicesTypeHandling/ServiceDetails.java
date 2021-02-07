

package com.nu.art.cyborg.demo.ui.controllers.servicesTypeHandling;

import android.app.Service;

/**
 * Created by TacB0sS on 18-May 2016.
 */
public class ServiceDetails {

	final Class<? extends Service> serviceType;

	int created;

	int binded;

	public ServiceDetails(Class<? extends Service> serviceType) {
		this.serviceType = serviceType;
	}

	@Override
	public String toString() {
		return "Created: " + created + "\nBinded: " + binded;
	}
}
