/*
 * The module-manager project, is THE infrastructure that all my frameworks
 *  are based on, it allows encapsulation of logic where needed, and allow
 *  modules to converse without other design patterns limitations.
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

package com.nu.art.modular.core;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class ValidationResult {

	private static final String Separator = "-------------------------------------------------------------------------------\n";

	private HashMap<Module, Vector<String>> validationErrorEntries = new HashMap<>();

	public final void addEntry(Module module, String entry) {
		Vector<String> errorEntries = validationErrorEntries.get(module);
		if (errorEntries == null) {
			errorEntries = new Vector<>();
			validationErrorEntries.put(module, errorEntries);
		}
		errorEntries.add(entry);
	}

	boolean isEmpty() {
		return validationErrorEntries.isEmpty();
	}

	String getErrorData() {
		Set<Module> keysSet = validationErrorEntries.keySet();
		Module[] keys = keysSet.toArray(new Module[keysSet.size()]);
		String errorData = Separator;
		for (Module module : keys) {
			errorData += "Error while validating module: " + module.getClass().getName() + "\n";
			Vector<String> errorEntries = validationErrorEntries.get(module);
			for (String errorEntry : errorEntries) {
				errorData += "    " + errorEntry + "\n";
			}
			errorData += Separator;
		}

		return errorData;
	}
}
