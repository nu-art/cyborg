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

public interface Converter {

	Object convert(String value)
		throws Exception;

	public static final Converter StringConverter = new Converter() {

		@Override
		public String convert(String value) {
			return value;
		}
	};

	public static final Converter IntegerConverter = new Converter() {

		@Override
		public Integer convert(String value) {
			return Integer.parseInt(value);
		}
	};

	public static final Converter LongConverter = new Converter() {

		@Override
		public Long convert(String value) {
			return Long.parseLong(value);
		}
	};

	public static final Converter ShortConverter = new Converter() {

		@Override
		public Short convert(String value) {
			return Short.parseShort(value);
		}
	};

	public static final Converter ByteConverter = new Converter() {

		@Override
		public Byte convert(String value) {
			return Byte.parseByte(value);
		}
	};

	public static final Converter FloatConverter = new Converter() {

		@Override
		public Float convert(String value) {
			return Float.parseFloat(value);
		}
	};

	public static final Converter DoubleConverter = new Converter() {

		@Override
		public Double convert(String value) {
			return Double.parseDouble(value);
		}
	};

	public static final Converter BooleanConverter = new Converter() {

		@Override
		public Boolean convert(String value) {
			return Boolean.parseBoolean(value);
		}
	};
}
