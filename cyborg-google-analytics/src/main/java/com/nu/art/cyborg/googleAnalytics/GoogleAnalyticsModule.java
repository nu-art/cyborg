/*
 * The google-analytics module, is an implementation of Google Analytics
 * SDK for Android, allowing one-liner analytics sending.
 *
 * Copyright (C) 2017  Adam van der Kruk aka TacB0sS
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
package com.nu.art.cyborg.googleAnalytics;

import android.Manifest.permission;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders.AppViewBuilder;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.HitBuilders.ExceptionBuilder;
import com.google.android.gms.analytics.Tracker;
import com.nu.art.core.tools.ExceptionTools;
import com.nu.art.cyborg.annotations.ModuleDescriptor;
import com.nu.art.cyborg.common.consts.AnalyticsConstants;
import com.nu.art.cyborg.core.CyborgModule;
import com.nu.art.cyborg.core.modules.IAnalyticsModule;
import com.nu.art.cyborg.modules.AppDetailsModule;

@SuppressWarnings("unused")
@ModuleDescriptor(usesPermissions = {
	permission.INTERNET,
	permission.ACCESS_NETWORK_STATE
},
                  dependencies = {AppDetailsModule.class})
public final class GoogleAnalyticsModule
	extends CyborgModule
	implements IAnalyticsModule, AnalyticsConstants {

	private static final int DefaultDispatchInterval = 30;

	private GoogleAnalytics googleAnalytics;

	private Tracker tracker;

	/**
	 * Dispatch interval in seconds
	 */
	private int dispatchInterval = DefaultDispatchInterval;

	private AppDetailsModule apkDetails;

	private String siteId;

	@Override
	protected final void init() {
		apkDetails = getModule(AppDetailsModule.class);
		googleAnalytics = GoogleAnalytics.getInstance(cyborg.getApplicationContext());
		googleAnalytics.setLocalDispatchPeriod(dispatchInterval);
		createTracker();
	}

	/**
	 * @param dispatchInterval Dispatch interval in seconds
	 */
	public void setDispatchInterval(int dispatchInterval) {
		this.dispatchInterval = dispatchInterval;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	@Override
	protected void printDetails() {
		logInfo("Analytics key selected: " + siteId);
	}

	private void createTracker() {
		if (tracker != null)
			return;

		tracker = googleAnalytics.newTracker(siteId);
		tracker.setAnonymizeIp(true);
	}

	@Override
	public synchronized final void sendEvent(String category, String action, String label, long value) {
		if (isTrackingDisabled())
			return;

		EventBuilder eventBuilder = new EventBuilder(category, action);
		eventBuilder.setLabel(label);
		eventBuilder.setValue(value);
		tracker.send(eventBuilder.build());
	}

	@Override
	public synchronized final void sendException(String description, Throwable t, boolean crash) {
		if (isTrackingDisabled())
			return;

		ExceptionBuilder exceptionBuilder = new ExceptionBuilder();
		exceptionBuilder.setDescription(description);
		exceptionBuilder.set("Exception", ExceptionTools.getStackTrace(t));
		exceptionBuilder.setFatal(crash);
		tracker.send(exceptionBuilder.build());
	}

	private boolean isTrackingDisabled() {
		return apkDetails == null || apkDetails.isAutomated();
	}

	@Override
	public synchronized final void sendView(String viewName) {
		if (isTrackingDisabled())
			return;

		tracker.setScreenName(viewName);
		AppViewBuilder screenViewBuilder = new AppViewBuilder();
		tracker.send(screenViewBuilder.build());
	}
}
