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

package com.nu.art.core.constants.language;

import java.util.Locale;

public enum Language {
	Afrikaans("Afrikaans", "af"),
	Albanian("Albanian", "sq"),
	Arabic("Arabic", "ar"),
	Azerbaijani("Azerbaijani", "az"),
	Basque("Basque", "eu"),
	Bengali("Bengali", "bn"),
	Belarusian("Belarusian", "be"),
	Bulgarian("Bulgarian", "bg"),
	Catalan("Catalan", "ca"),
	ChineseSimplified("Chinese Simplified", "zh-CN"),
	ChineseTraditional("Chinese Traditional", "zh-TW"),
	Croatian("Croatian", "hr"),
	Czech("Czech", "cs"),
	Danish("Danish", "da"),
	Dutch("Dutch", "nl"),
	English("English", "en"),
	Esperanto("Esperanto", "eo"),
	Estonian("Estonian", "et"),
	Filipino("Filipino", "tl"),
	Finnish("Finnish", "fi"),
	French("French", "fr"),
	Galician("Galician", "gl"),
	Georgian("Georgian", "ka"),
	German("German", "de"),
	Greek("Greek", "el"),
	Gujarati("Gujarati", "gu"),
	HaitianCreole("Haitian Creole", "ht"),
	Hebrew("Hebrew", "iw"),
	Hindi("Hindi", "hi"),
	Hungarian("Hungarian", "hu"),
	Icelandic("Icelandic", "is"),
	Indonesian("Indonesian", "id"),
	Irish("Irish", "ga"),
	Italian("Italian", "it"),
	Japanese("Japanese", "ja"),
	Kannada("Kannada", "kn"),
	Korean("Korean", "ko"),
	Latin("Latin", "la"),
	Latvian("Latvian", "lv"),
	Lithuanian("Lithuanian", "lt"),
	Macedonian("Macedonian", "mk"),
	Malay("Malay", "ms"),
	Maltese("Maltese", "mt"),
	Norwegian("Norwegian", "no"),
	Persian("Persian", "fa"),
	Polish("Polish", "pl"),
	Portuguese("Portuguese", "pt"),
	Romanian("Romanian", "ro"),
	Russian("Russian", "ru"),
	Serbian("Serbian", "sr"),
	Slovak("Slovak", "sk"),
	Slovenian("Slovenian", "sl"),
	Spanish("Spanish", "es"),
	Swahili("Swahili", "sw"),
	Swedish("Swedish", "sv"),
	Tamil("Tamil", "ta"),
	Telugu("Telugu", "te"),
	Thai("Thai", "th"),
	Turkish("Turkish", "tr"),
	Ukrainian("Ukrainian", "uk"),
	Urdu("Urdu", "ur"),
	Vietnamese("Vietnamese", "vi"),
	Welsh("Welsh", "cy"),
	Yiddish("Yiddish", "yi"),
	;

	private final String name;

	private final String localeString;

	private Language(String name, String localeString) {
		this.name = name;
		this.localeString = localeString;
	}

	public String getName() {
		return name;
	}

	public String getLocaleString() {
		return localeString;
	}

	public Locale getLocale() {
		String[] localeParams = localeString.split("_");
		if (localeParams.length == 1)
			return new Locale(localeParams[0]);

		return new Locale(localeParams[0], localeParams[1]);
	}

	public static final Language getInstanceByLocale(String locale) {
		for (Language langLocale : values()) {
			if (langLocale.localeString.equals(locale))
				return langLocale;
		}
		throw new EnumConstantNotPresentException(Language.class, locale);
	}
}
