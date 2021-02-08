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

public enum BT_AdvertiseState {
	Unknown("Unknown", -1),
	Advertising("Advertising Started", BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE),
	Connectable("Connectable", BluetoothAdapter.SCAN_MODE_CONNECTABLE),
	CancelAdvertising("Advertising Canceled", BluetoothAdapter.SCAN_MODE_NONE),
	;

	public static BT_AdvertiseState getInstanceForState(int newState) {
		BT_AdvertiseState[] states = BT_AdvertiseState.values();
		for (BT_AdvertiseState btState : states)
			if (btState.stateValue == newState)
				return btState;
		throw new EnumConstantNotPresentException(BT_AdvertiseState.class, "For state value=" + newState);
	}

	private final int stateValue;

	private String stateLabel;

	BT_AdvertiseState(String stateLabel, int stateValue) {
		this.stateValue = stateValue;
		this.stateLabel = stateLabel;
	}

	public String getStateLabel() {
		return stateLabel;
	}
}
