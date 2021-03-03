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

import com.nu.art.belog.BeConfig.LoggerConfig;
import com.nu.art.belog.consts.LogLevel;
import com.nu.art.belog.interfaces.LogComposer;

@SuppressWarnings( {
	                   "unused",
	                   "BooleanMethodIsAlwaysInverted"
                   })
public abstract class LoggerClient<Config extends LoggerConfig> {

	protected LogComposer composer = new DefaultLogComposer();
	protected Config config;

	public final void setConfig(Config config) {
		this.config = config;
	}

	public final Config getConfig() {
		return config;
	}

	public final void setComposer(LogComposer composer) {
		this.composer = composer;
	}

	protected void init() { }

	final void _log(long timestamp, LogLevel level, Thread thread, String tag, String message, Throwable t) {
		log(timestamp, level, thread, tag, message, t);
	}

	protected abstract void log(long timestamp, LogLevel level, Thread thread, String tag, String message, Throwable t);

	protected void dispose() {}

	protected String composeEntry(long timestamp, LogLevel level, Thread thread, String tag, String message, Throwable t) {
		return composer.composeEntry(timestamp, level, thread, tag, message, t);
	}
}
