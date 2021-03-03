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

package com.nu.art.belog.loggers;

import com.nu.art.belog.consts.LogLevel;

class LogEntry {

	long timestamp;

	LogLevel level;

	Thread thread;

	String tag;

	String message;

	Throwable t;

	LogEntry set(long timestamp, LogLevel level, Thread thread, String tag, String message, Throwable t) {
		this.timestamp = timestamp;
		this.level = level;
		this.thread = thread;
		this.tag = tag;
		this.message = message;
		this.t = t;
		return this;
	}

	@Override
	public String toString() {
		return message;
	}
}