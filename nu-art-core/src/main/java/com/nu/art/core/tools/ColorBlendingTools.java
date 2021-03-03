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

public class ColorBlendingTools {

	public static final class ColorBean {

		public int r, g, b, a;

		public ColorBean(int r, int g, int b, int a) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}

		public ColorBean(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}

	public synchronized static ColorBean linearBlending(ColorBean color1, ColorBean color2) {
		return ColorBlendingTools.linearBlending(color1, 0.5, color2, 0.5);
	}

	public synchronized static ColorBean linearBlending(ColorBean color1, double color1Factor, ColorBean color2, double color2Factor) {
		return new ColorBean((int) (color1.r * color1Factor + color2.r * color2Factor) / 2, (int) (color1.g * color1Factor + color2.g * color2Factor) / 2, (int) (color1.b * color1Factor + color2.b * color2Factor) / 2);
	}
}
