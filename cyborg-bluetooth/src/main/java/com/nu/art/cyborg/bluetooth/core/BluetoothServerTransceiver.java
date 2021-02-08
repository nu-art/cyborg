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
import android.bluetooth.BluetoothServerSocket;

import com.nu.art.io.PacketSerializer;
import com.nu.art.io.SocketWrapper;

import java.io.IOException;
import java.util.UUID;

public class BluetoothServerTransceiver
	extends BluetoothTransceiver {

	private final String name;

	private final BluetoothAdapter btAdapter;

	private BluetoothServerSocket serverSocket;

	BluetoothServerTransceiver(BluetoothAdapter btAdapter, String name, String uuid, PacketSerializer packetSerializer) {
		super(name, uuid, packetSerializer);
		this.name = name;
		this.btAdapter = btAdapter;
	}

	public final SocketWrapper connectImpl()
		throws IOException {
		serverSocket = btAdapter.listenUsingInsecureRfcommWithServiceRecord(name, UUID.fromString(uuid));
		return new BluetoothSocketWrapper(serverSocket.accept(-1));
	}

	public void disconnectImpl() {
		try {
			if (serverSocket != null)
				serverSocket.close();
		} catch (IOException e) {
			notifyError(e);
		}
	}

	@Override
	protected String extraLog() {
		return "UUID: " + uuid;
	}
}
