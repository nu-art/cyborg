

package com.nu.art.cyborg.demo.ui.controllers;

import android.Manifest.permission;
import android.net.wifi.WifiInfo;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nu.art.core.generics.Processor;
import com.nu.art.cyborg.annotations.ItemType;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgRecycler;
import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.core.dataModels.ListDataModel;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.PermissionModule;
import com.nu.art.cyborg.modules.wifi.WifiItem_Scanner.OnWifiUIListener;
import com.nu.art.cyborg.modules.wifi.WifiItem_Scanner.ScannedWifiInfo;
import com.nu.art.cyborg.modules.wifi.WifiModule;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

@ReflectiveInitialization
public class ControllerV1_WifiExample
	extends CyborgController
	implements OnWifiUIListener {

	@ViewIdentifier(viewId = R.id.WifiExample,
	                listeners = {ViewListener.OnRecyclerItemClicked})
	private CyborgRecycler recycler;

	@ViewIdentifier(viewId = R.id.TV_ScanState)
	private TextView scanState;

	@ViewIdentifier(viewId = R.id.TV_CurrentWifiState)
	private TextView currentWifiState;

	@ViewIdentifier(viewId = R.id.BTN_Scan,
	                listeners = ViewListener.OnClick)
	private Button scanButton;

	@ViewIdentifier(viewId = R.id.BTN_Adapter,
	                listeners = ViewListener.OnClick)
	private Button adapterButton;

	private WifiModule wifiModule;

	private ControllerV1_WifiExample() {
		super(R.layout.controller__wifi_example);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate() {
		setupRecycler(recycler, new Processor<ListDataModel<ScannedWifiInfo>>() {
			@Override
			public void process(ListDataModel<ScannedWifiInfo> wifis) {
				wifis.add(wifiModule.getScanResults());
			}
		}, WifiResultRenderer.class);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.BTN_Scan:
				wifiModule.listenForWifiNetworks(!wifiModule.isListenForWifiNetworks());
				break;

			case R.id.BTN_Adapter:
				if (!wifiModule.isAdapterEnabled())
					wifiModule.enableAdapter();
				else
					wifiModule.disableAdapter();
				break;
		}
		renderUI();
	}

	@Override
	protected void onResume() {
		super.onResume();

		PermissionModule module = getModule(PermissionModule.class);
		if (!module.isPermissionGranted(permission.ACCESS_FINE_LOCATION)) {
			module.requestPermission(100, permission.ACCESS_FINE_LOCATION);
			return;
		}

		renderUI();
	}

	@Override
	protected void render() {
		adapterButton.setText(wifiModule.isAdapterEnabled() ? "Turn OFF" : "Turn ON");
		scanButton.setText(wifiModule.isListenForWifiNetworks() ? "Scanning" : "Scan");
		String scanState = wifiModule.isListenForWifiNetworks() ? "Scanning..." : "Idle";
		scanState += "\n" + (wifiModule.isAdapterEnabled() ? "Adapter ON" : "Adapter OFF");
		this.scanState.setText(scanState);

		String currentWifiState = "";
		if (!wifiModule.isConnectedToWifi())
			currentWifiState = "Not Connected";
		else {
			String connectedWifiName = wifiModule.getConnectedWifiName();
			WifiInfo wifiInfo = wifiModule.getWifiConnectionInfo();
			if (wifiInfo == null)
				currentWifiState += "Connected.. need to scan wifis";
			else {
				currentWifiState += "Name:" + connectedWifiName + "\n";
				currentWifiState += "SSID:" + wifiInfo.getSSID() + "\n";
				currentWifiState += "BSSID:" + wifiInfo.getBSSID() + "\n";
				currentWifiState += "MacAddress:" + wifiInfo.getMacAddress() + "\n";
				currentWifiState += "Frequency:" + wifiInfo.getFrequency() + "\n";
				currentWifiState += "HiddenSSID:" + wifiInfo.getHiddenSSID() + "\n";
				currentWifiState += "IpAddress:" + wifiInfo.getIpAddress() + "\n";
				currentWifiState += "LinkSpeed:" + wifiInfo.getLinkSpeed() + "\n";
				currentWifiState += "NetworkId:" + wifiInfo.getNetworkId() + "\n";
				currentWifiState += "Rssi:" + wifiInfo.getRssi() + "\n";
				currentWifiState += "SupplicantState:" + wifiInfo.getSupplicantState() + "\n";
			}
		}

		this.currentWifiState.setText(currentWifiState);
	}

	@Override
	public void onScanCompleted() {
		renderUI();
		recycler.invalidateDataModel();
	}

	@ItemType(type = ScannedWifiInfo.class)
	private static class WifiResultRenderer
		extends ItemRenderer<ScannedWifiInfo> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		public WifiResultRenderer() {
			super(R.layout.renderer__wifi_network);
		}

		@Override
		protected void renderItem(ScannedWifiInfo info) {
			String text = "";
			text += "SSID:" + info.scanResult.SSID + "\n";
			text += "level:" + info.scanResult.level + "\n";
			text += "capabilities:" + info.scanResult.capabilities + "\n";
			text += "BSSID:" + info.scanResult.BSSID + "\n";
			text += "timestamp:" + info.scanResult.timestamp + "\n";
			text += "frequency:" + info.scanResult.frequency + "\n";
			exampleLabel.setText(text);
		}
	}
}