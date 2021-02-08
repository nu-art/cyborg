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

import android.bluetooth.BluetoothSocket;

import com.nu.art.cyborg.bluetooth.exceptions.BluetoothConnectionException;

import java.lang.reflect.Method;
import java.util.UUID;

public enum ConnectivityType {
	ReflectiveSecure {
		@Override
		protected BluetoothSocket createSocket(CyborgBT_Device device)
			throws BluetoothConnectionException {
			Method m;
			try {
				device.logInfo("+---+ Fetching BT RFcomm Socket workaround index " + 1 + "...");
				m = device.getBluetoothDevice().getClass().getMethod("createRfcommSocket", new Class[]{int.class});
				return (BluetoothSocket) m.invoke(device.getBluetoothDevice(), 1);
			} catch (Exception e) {
				throw new BluetoothConnectionException("Error Fetching BT RFcomm Socket!", e);
			}
		}
	},

	ReflectiveInsecure {
		@Override
		protected BluetoothSocket createSocket(CyborgBT_Device device)
			throws BluetoothConnectionException {
			Method m;
			try {
				device.logInfo("+---+ Fetching BT insecure RFcomm Socket workaround index " + 1 + "...");
				m = device.getBluetoothDevice().getClass().getMethod("createInsecureRfcommSocket", new Class[]{int.class});
				return (BluetoothSocket) m.invoke(device.getBluetoothDevice(), 1);
			} catch (Exception e) {
				throw new BluetoothConnectionException("Error Fetching BT RFcomm Socket!", e);
			}
		}
	},

	Secured {
		@Override
		protected BluetoothSocket createSocket(CyborgBT_Device device)
			throws BluetoothConnectionException {
			try {
				device.logInfo("+---+ Fetching BT RFcomm Socket standard for UUID: " + device.uuid + "...");
				return device.getBluetoothDevice().createRfcommSocketToServiceRecord(UUID.fromString(device.uuid));
			} catch (Exception e) {
				throw new BluetoothConnectionException("Error Fetching BT RFcomm Socket!", e);
			}
		}
	},

	Insecure {
		@Override
		protected BluetoothSocket createSocket(CyborgBT_Device device)
			throws BluetoothConnectionException {
			try {
				device.logInfo("+---+ Fetching BT Insecure RFcomm Socket standard for UUID: " + device.uuid + "...");
				return device.getBluetoothDevice().createInsecureRfcommSocketToServiceRecord(UUID.fromString(device.uuid));
			} catch (Exception e) {
				throw new BluetoothConnectionException("Error Fetching BT Insecure RFcomm Socket!", e);
			}
		}
	};

	protected abstract BluetoothSocket createSocket(CyborgBT_Device device)
		throws BluetoothConnectionException;
}