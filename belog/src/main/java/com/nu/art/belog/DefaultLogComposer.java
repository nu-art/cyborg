/*
 * belog is an extendable infrastructure to manage and customize
 * your application output.
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

package com.nu.art.belog;

import com.nu.art.belog.consts.LogLevel;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.tools.ExceptionTools;
import com.nu.art.core.utils.SynchronizedObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TacB0sS on 27-Feb 2017.
 */
public class DefaultLogComposer
	implements com.nu.art.belog.interfaces.LogComposer {

	private final static SimpleDateFormat DefaultTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
	private SynchronizedObject<StringBuilder> buffers = new SynchronizedObject<>(new Getter<StringBuilder>() {
		@Override
		public StringBuilder get() {
			return new StringBuilder();
		}
	});

	private final Date date = new Date();

	@Override
	public synchronized String composeEntry(long timestamp, LogLevel level, Thread thread, String tag, String message, Throwable t) {
		date.setTime(timestamp);

		StringBuilder buffer = buffers.get();
		buffer.setLength(0);
		buffer.append(DefaultTimeFormat.format(date)).append(" ");
		buffer.append(level).append("/");
		buffer.append(thread.getName()).append("/");
		buffer.append(tag).append(": ");

		if (message != null) {
			buffer.append(message);
			if (!message.endsWith("\n"))
				buffer.append("\n");
		}

		if (t != null)
			buffer.append(ExceptionTools.getStackTrace(t)).append("\n");

		return buffer.toString();
	}
}
