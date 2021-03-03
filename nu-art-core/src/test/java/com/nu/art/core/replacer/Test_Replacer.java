/*
 * cyborg-core is an extendable  module based framework for Android.
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

package com.nu.art.core.replacer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nu.art.core.archiver.ArchiveReader;
import com.nu.art.core.archiver.ArchiveReader.OverridePolicy;
import com.nu.art.core.archiver.ArchiveWriter;
import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.exceptions.runtime.ThisShouldNotHappenException;
import com.nu.art.core.file.Charsets;
import com.nu.art.core.tools.FileTools;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.nu.art.core.archiver.ArchiveReader.OverridePolicy.ForceDelete;
import static com.nu.art.core.tools.FileTools.getRunningDirectoryPath;
import static com.nu.art.core.tools.FileTools.isParentOfRunningFolder;

/**
 * Created by TacB0sS on 05/04/2018.
 */

public class Test_Replacer {

	private final Replacer replacer = Replacer.Replacer;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private String getTestName() {
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
		return getClass().getSimpleName() + "." + stackTraceElement.getMethodName();
	}

	protected final void testSuccess(String original, String expected, Object repo) {
		System.out.println();
		System.out.println("------------ " + getTestName() + " --------------");
		System.out.println("Original: " + original);
		System.out.println("Expected: " + expected);
		System.out.println("Repo: " + gson.toJson(repo));

		String result = replacer.replace(original, repo);
		if (!result.equals(expected))
			throw new ThisShouldNotHappenException("Test failed!");
	}

	protected final void testFail(String original, Object repo) {
		System.out.println();
		System.out.println("------------ " + getTestName() + " --------------");
		System.out.println("Original: " + original);
		System.out.println("Repo: " + gson.toJson(repo));

		try {
			replacer.replace(original, repo);
		} catch (Exception e) {
			return;
		}

		throw new ThisShouldNotHappenException("This test should fail!!");
	}

	@Test
	public void testNoParam_NoRepo() {
		testSuccess("Nothing here", "Nothing here", null);
	}

	@Test
	public void testWithParam_NoRepo() {
		testFail("Nothing ${here}", null);
	}

	@Test
	public void testWith_1_Level_Param__HashMap() {
		HashMap<String, Object> repo = new HashMap<>();
		repo.put("here", "there");
		testSuccess("Nothing ${here}", "Nothing there", repo);
	}

	@Test
	public void testWith_2_Level_Param__Nested_HashMap() {
		HashMap<String, Object> repo = new HashMap<>();
		HashMap<String, Object> repo1 = new HashMap<>();
		repo1.put("re", "there");
		repo.put("he", repo1);
		testSuccess("Nothing ${he.re}", "Nothing there", repo);
	}
}
