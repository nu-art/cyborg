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

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.io.PacketSerializer;
import com.nu.art.io.SocketWrapper;

import java.io.IOException;

public class CyborgBT_Device
	extends BluetoothTransceiver {

	private final BluetoothDevice bluetoothDevice;

	private ConnectivityType type = ConnectivityType.Insecure;

	public CyborgBT_Device(BluetoothDevice bluetoothDevice, String uuid, PacketSerializer packetSerializer) {
		this(bluetoothDevice.getName(), bluetoothDevice, uuid, packetSerializer);
	}

	public CyborgBT_Device(String name, BluetoothDevice bluetoothDevice, String uuid, PacketSerializer packetSerializer) {
		super(bluetoothDevice.getName(), uuid, packetSerializer);
		this.bluetoothDevice = bluetoothDevice;
	}

	protected final BluetoothDevice getBluetoothDevice() {
		return bluetoothDevice;
	}

	public SocketWrapper connectImpl()
		throws IOException {
		setOneShot();

		logInfo("+---+ Connecting to device...");
		if (socket != null)
			throw new BadImplementationException("Error socket is not null!!");

		BluetoothSocket socket = type.createSocket(this);
		logInfo("+---+ Connecting to socket...");

		try {
			socket.connect();
		} catch (IOException e) {
			socket.close();
			throw e;
		}
		logInfo("+---+ Connected to socket");
		return new BluetoothSocketWrapper(socket);
	}

	public final String getName() {
		return bluetoothDevice.getName();
	}

	public final String getAddress() {
		return bluetoothDevice.getAddress();
	}

	@Override
	public String toString() {
		String toRet = "";
		toRet += getName() + "@[" + getAddress() + "]:<" + getState().name() + ">";
		return toRet;
	}

	@Override
	protected String extraLog() {
		return "@[" + getAddress() + "]:<" + getState().name() + ">";
	}

	public void setSocketType(ConnectivityType type) {
		this.type = type;
	}
}
