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

import android.bluetooth.BluetoothAdapter;

public enum BT_AdapterState {
	NotAvailable("Device does not support Bluetooth", 0),
	Off("Bluetooth Off", BluetoothAdapter.STATE_OFF),
	TurningOn("Turning Bluetooth On", BluetoothAdapter.STATE_TURNING_ON),
	On("Bluetooth On", BluetoothAdapter.STATE_ON),
	TurningOff("Turning Bluetooth Off", BluetoothAdapter.STATE_TURNING_OFF),
	;

	public static BT_AdapterState getInstanceForState(int newState) {
		BT_AdapterState[] states = BT_AdapterState.values();
		for (BT_AdapterState btState : states)
			if (btState.stateValue == newState)
				return btState;
		throw new EnumConstantNotPresentException(BT_AdapterState.class, "For state value=" + newState);
	}

	private final int stateValue;

	private String stateLabel;

	BT_AdapterState(String stateLabel, int stateValue) {
		this.stateValue = stateValue;
		this.stateLabel = stateLabel;
	}

	public String getStateLabel() {
		return stateLabel;
	}
}
