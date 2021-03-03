/*
 * The core of the core of all my projects!
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

package com.nu.art.core.utils;

import com.nu.art.core.tools.ArrayTools;

import java.util.HashSet;

@SuppressWarnings("unused")
public class DebugFlags {

	private static final DebugFlags instance = new DebugFlags();

	private final HashSet<DebugFlag> AllDebugFlags = new HashSet<>();
	private final HashSet<String> ActiveDebugFlags = new HashSet<>();

	private DebugFlags() { }

	public static DebugFlag createFlag(Class<?> forType) {
		return instance.new DebugFlagImpl(forType);
	}

	public static DebugFlag createFlag(String key) {
		return instance.new DebugFlagImpl(key);
	}

	public static DebugFlag[] getAllFlags() {
		return ArrayTools.asArray(instance.AllDebugFlags, DebugFlag.class);
	}

	private class DebugFlagImpl
		implements DebugFlag {

		private Class<?> type;
		private final String flag;

		private DebugFlagImpl(Class<?> forType) {
			this(forType.getSimpleName());
		}

		private DebugFlagImpl(String key) {
			flag = key;
			AllDebugFlags.add(this);
		}

		@Override
		public String getName() {
			return flag;
		}

		@Override
		public void enable(boolean enable) {
			if (enable)
				enable();
			else
				disable();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			DebugFlagImpl debugFlag = (DebugFlagImpl) o;

			if (type != null ? !type.equals(debugFlag.type) : debugFlag.type != null)
				return false;

			return flag != null ? flag.equals(debugFlag.flag) : debugFlag.flag == null;
		}

		@Override
		public int hashCode() {
			int result = type != null ? type.hashCode() : 0;
			result = 31 * result + (flag != null ? flag.hashCode() : 0);
			return result;
		}

		@Override
		public void enable() {
			ActiveDebugFlags.add(flag);
		}

		@Override
		public void disable() {
			ActiveDebugFlags.remove(flag);
		}

		@Override
		public boolean isEnabled() {
			return ActiveDebugFlags.contains(flag);
		}
	}

	public interface DebugFlag {

		String getName();

		void enable(boolean enable);

		void enable();

		void disable();

		boolean isEnabled();
	}
}
