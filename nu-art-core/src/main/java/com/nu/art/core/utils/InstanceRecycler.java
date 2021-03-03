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

import java.util.Stack;

/**
 * Created by TacB0sS on 27-Feb 2017.
 */

public class InstanceRecycler<Type> {

	public interface Instantiator<Type> {

		Type create();
	}

	private final Instantiator<Type> instantiator;
	private final Stack<Type> recyclables = new Stack<>();

	public InstanceRecycler(Instantiator<Type> instantiator) {this.instantiator = instantiator;}

	public final synchronized Type getInstance() {
		if (recyclables.size() == 0)
			return instantiator.create();

		return recyclables.pop();
	}

	public final synchronized void recycle(Type item) {
		recyclables.add(item);
	}
}
