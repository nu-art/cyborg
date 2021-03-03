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

package com.nu.art.core.tools;

/**
 * Created by TacB0sS on 10-Sep 2016.
 */

public class MathTools {

	private MathTools() {
		throw new com.nu.art.core.exceptions.runtime.BadImplementationException("Do not instantiate this object");
	}

	public static float calcAverage(float[] values) {
		float sum = sum(values);
		return sum / values.length;
	}

	public static float sum(float[] values) {
		float sum = 0;
		for (float value : values) {
			sum += value;
		}
		return sum;
	}
}
