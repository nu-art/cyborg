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
package com.nu.art.cyborg.bluetooth.constants;

import android.bluetooth.BluetoothDevice;

public enum BT_ConnectionState {
	ACL_Connecting("ACL Connecting"),
	ACL_Connected(BluetoothDevice.ACTION_ACL_CONNECTED, "ACL Connected"),
	Bonding(BluetoothDevice.ACTION_BOND_STATE_CHANGED, "New Bonding State"),
	PairingRequest("android.bluetooth.device.action.PAIRING_REQUEST", "Paring request"),
	SPP_Connected("", "SPP Connected"),
	ACL_Disconnecting(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED, "ACL Disconnecting"),
	ACL_Disconnected(BluetoothDevice.ACTION_ACL_DISCONNECTED, "ACL Disconnected");

	private static final String NoAndroidConstant = "No Android Action";

	private String label;

	private String action;

	BT_ConnectionState(String label) {
		this(NoAndroidConstant, label);
	}

	BT_ConnectionState(String action, String label) {
		this.label = label;
		this.action = action;
	}

	public String getLabel() {
		return label;
	}

	public String getAction() {
		return action;
	}

	public static BT_ConnectionState getStateByAction(String action) {
		BT_ConnectionState[] states = values();
		for (BT_ConnectionState state : states)
			if (state.action.equals(action))
				return state;
		throw new EnumConstantNotPresentException(BT_ConnectionState.class, action);
	}
}
