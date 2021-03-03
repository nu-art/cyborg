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

import java.util.Collections;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find a regex in a string.
 *
 * @author TacB0sS
 */
public class RegexAnalyzer {

	public static final class RegexValidators {

		public static final String EmailRegexValidation = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	}

	/**
	 * Check if the supplied text contains any of the supplied Regex strings.
	 *
	 * @param caseSensitive Is looking for a case sensitive regex or not.
	 * @param fullText      The full text to search for the supplied Regex array in.
	 * @param regexToFind   An array of Regex to find any of them in the full text supplied.
	 *
	 * @return Whether the supplied text contains any of the supplied Regex.
	 */
	public static final boolean containsRegex(String fullText, boolean caseSensitive, String... regexToFind) {
		return RegexAnalyzer.findRegex(fullText, caseSensitive, false, regexToFind).length > 0;
	}

	private static final String[] findRegex(String fullText, boolean caseSensitive, boolean stopOnFirst, String... regexToFind) {
		Vector<String> toRet = new Vector<>();
		RegexAnalyzer fr = new RegexAnalyzer("", caseSensitive);
		for (String regex : regexToFind) {
			fr.setRegex(regex);
			String[] instances = fr.instances(fullText);
			Collections.addAll(toRet, instances);
			if (stopOnFirst && toRet.size() > 0) {
				break;
			}
		}
		return toRet.toArray(new String[0]);
	}

	/**
	 * By default searches for all the Regex Instance for the first Regex Group, which is the entire Regex string
	 * supplied.
	 *
	 * @param caseSensitive Is looking for a case sensitive regex or not.
	 * @param fullText      The full text to search for the supplied Regex array in.
	 * @param regexToFind   An array of Regex to find any of them in the full text supplied.
	 *
	 * @return An array of the found results.
	 */
	public static final String[] findRegex(String fullText, boolean caseSensitive, String... regexToFind) {
		return RegexAnalyzer.findRegex(fullText, caseSensitive, false, regexToFind);
	}

	protected boolean caseSensitive = true;

	private String toFind;

	/**
	 * @param toFind The string to find.
	 */
	public RegexAnalyzer(String toFind) {
		setRegex(toFind);
	}

	/**
	 * Find a text in the output string
	 *
	 * @param toFind        The string to find.
	 * @param caseSensitive Is looking for a case sensitive regex or not.
	 */
	public RegexAnalyzer(String toFind, boolean caseSensitive) {
		this(toFind);
		this.caseSensitive = caseSensitive;
	}

	/**
	 * @param instance   The index of the instance for which to get the regex group.
	 * @param groupIndex The group index to get from the regex instance.
	 * @param fullText   The full text to search for the regex in.
	 *
	 * @return The string of the specified group, from the specified instance of the complete regex.
	 */
	public final String findRegex(int instance, int groupIndex, String fullText) {
		String[] regexps = findRegex(instance, fullText, groupIndex);
		if (regexps.length == 0) {
			return null;
		}
		return regexps[0];
	}

	/**
	 * @param instance     The index of the instance for which to get the regex group.
	 * @param fullText     The full text to search for the regex in.
	 * @param groupIndices The group indices to get from the regex instance.
	 *
	 * @return The strings of the specified group, from the specified instance of the complete regex.
	 */
	public final String[] findRegex(int instance, String fullText, int... groupIndices) {
		int instanceIndex = 0;
		Pattern p = caseSensitive ? Pattern.compile(toFind) : Pattern.compile(toFind, Pattern.CASE_INSENSITIVE);
		while (instanceIndex <= instance) {
			Matcher m = p.matcher(fullText);
			if (!m.find()) {
				break;
			}
			if (instanceIndex == instance) {
				String[] toRet = new String[groupIndices.length];
				for (int i = 0; i < groupIndices.length; i++) {
					toRet[i] = m.group(groupIndices[i]);
				}
				return toRet;
			}
			instanceIndex++;
			fullText = fullText.replaceFirst(toFind, "");
		}
		return new String[0];
	}

	/**
	 * @param fullText The text to check for the regex.
	 *
	 * @return The count of the instances of the full regex.
	 */
	public final int instanceCount(String fullText) {
		return instances(fullText).length;
	}

	/**
	 * @param fullText The text to check for the regex.
	 *
	 * @return The instances of the full regex.
	 */
	public final String[] instances(String fullText) {
		// int instanceIndex = 0;
		Vector<String> instances = new Vector<>();
		String tempStr;
		Pattern p = caseSensitive ? Pattern.compile(toFind) : Pattern.compile(toFind, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(fullText);
		while (true) {
			if (!m.find()) {
				break;
			}
			tempStr = m.group(0);
			if (tempStr != null && tempStr.length() > 0) {
				instances.add(tempStr);
			}
			// fullText = fullText.replaceFirst(toFind, "");
		}
		return instances.toArray(new String[0]);
	}

	private void setRegex(String regex) {
		toFind = regex;
	}
}
