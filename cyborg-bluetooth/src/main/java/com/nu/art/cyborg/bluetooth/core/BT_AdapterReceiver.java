/*
 * The cyborg-bluetooth module, meant to simplify your life when
 * dealing with connecting to remote Android devices, and stream data...
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
package com.nu.art.cyborg.bluetooth.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.nu.art.cyborg.bluetooth.constants.BT_AdapterState;
import com.nu.art.cyborg.bluetooth.constants.BT_AdvertiseState;
import com.nu.art.cyborg.bluetooth.constants.BT_ConnectionState;
import com.nu.art.cyborg.bluetooth.constants.BT_InquiringState;
import com.nu.art.cyborg.core.CyborgReceiver;

public class BT_AdapterReceiver
	extends CyborgReceiver<BluetoothModule> {

	private static final String[] DefaultActions = new String[]{
		BluetoothDevice.ACTION_FOUND,
		BluetoothAdapter.ACTION_DISCOVERY_STARTED,
		BluetoothAdapter.ACTION_SCAN_MODE_CHANGED,
		BluetoothAdapter.ACTION_STATE_CHANGED,
		BluetoothAdapter.ACTION_DISCOVERY_FINISHED,
		BluetoothDevice.ACTION_ACL_CONNECTED,
		BluetoothDevice.ACTION_ACL_DISCONNECTED,
		BluetoothDevice.ACTION_BOND_STATE_CHANGED,
		//BluetoothDevice.ACTION_PAIRING_REQUEST,
		"android.bluetooth.device.action.PAIRING_REQUEST",
		BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED
	};

	public BT_AdapterReceiver() {
		super(BluetoothModule.class, DefaultActions);
	}

	@Override
	protected void onReceive(Intent intent, BluetoothModule module) {
		String action = intent.getAction();
		switch (action) {
			case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
				int newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
				module.setState(BT_AdvertiseState.getInstanceForState(newState));
				break;

			case BluetoothAdapter.ACTION_STATE_CHANGED:
				newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
				module.setState(BT_AdapterState.getInstanceForState(newState));
				break;

			case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
				module.setState(BT_InquiringState.Inquiring);
				break;
			case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
				module.setState(BT_InquiringState.InquiringEnded);
				break;
			case BluetoothDevice.ACTION_FOUND:
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				module.newDeviceDetected(device);
				break;

			default:
				logDebug("ACL State: " + action);
				BT_ConnectionState connectionState = BT_ConnectionState.getStateByAction(action);
				device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				module.deviceStateChange(device, connectionState);
				break;
		}
	}
}
