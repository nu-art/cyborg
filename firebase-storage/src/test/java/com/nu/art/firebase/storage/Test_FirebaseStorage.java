package com.nu.art.firebase.storage;

import org.junit.Test;

public class Test_FirebaseStorage
	extends Test_StorageBase {

	@Test
	public void uploadFile() {
		createAsyncScenario()
			.addTest(uploadFileTest("sample-image.jpg", "image/jpg", "test/sample-image.jpg"))
			.execute();
	}

	@Test
	public void downloadFile() {
		createAsyncScenario()
			.addTest(downloadFileTest("sample-image.jpg", "test/sample-image.jpg"))
			.execute();
	}

	@Test
	public void checkList() {
		String[] files = {
			"sample-image-0.jpg",
			"sample-image-1.jpg",
			"sample-image-2.jpg",
			"sample-image-3.jpg"
		};

		AsyncScenario uploadFiles = createAsyncScenario();
		for (String file : files) {
			uploadFiles.addTest(uploadFileTest("sample-image.jpg", "image/jpg", "test-list/" + file));
		}

		createScenario()
			.addTest(uploadFiles)
			.addTest(listFiles("test-list/", files))
			.execute();
	}
}
