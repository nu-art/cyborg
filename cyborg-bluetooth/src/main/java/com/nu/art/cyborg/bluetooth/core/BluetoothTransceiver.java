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

import com.nu.art.io.BaseTransceiver;
import com.nu.art.io.PacketSerializer;
import com.nu.art.io.SocketWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BluetoothTransceiver
	extends BaseTransceiver {

	class BluetoothSocketWrapper
		implements SocketWrapper {

		final BluetoothSocket socket;

		BluetoothSocketWrapper(BluetoothSocket socket)
			throws IOException {
			this.socket = socket;
		}

		@Override
		public OutputStream getOutputStream()
			throws IOException {
			return socket.getOutputStream();
		}

		@Override
		public InputStream getInputStream()
			throws IOException {
			return socket.getInputStream();
		}

		@Override
		public void close()
			throws IOException {
			socket.close();
		}

		@Override
		public boolean isConnected()
			throws IOException {
			return socket.isConnected();
		}
	}

	final String uuid;

	BluetoothTransceiver(String name, String uuid, PacketSerializer packetSerializer) {
		super(name, packetSerializer);
		this.uuid = uuid;
	}
}