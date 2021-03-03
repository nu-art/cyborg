package com.nu.art.firebase.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.generics.Processor;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.tools.ArrayTools;
import com.nu.art.core.tools.FileTools;
import com.nu.art.core.tools.StreamTools;
import com.nu.art.firebase.storage.Module_FirebaseStorage.CompletionListener;
import com.nu.art.firebase.storage.Module_FirebaseStorage.DownloadListener;
import com.nu.art.firebase.storage.Module_FirebaseStorage.FirebaseBucket;
import com.nu.art.firebase.storage.Module_FirebaseStorage.FirebaseBucket.DownloadTransaction;
import com.nu.art.firebase.storage.Module_FirebaseStorage.FirebaseBucket.ListFiles;
import com.nu.art.firebase.storage.Module_FirebaseStorage.FirebaseBucket.UploadTransaction;
import com.nu.art.firebase.storage.Module_FirebaseStorage.ListFilesListener;
import com.nu.art.firebase.storage.Module_FirebaseStorage.RemoteFile;
import com.nu.art.firebase.storage.Module_FirebaseStorage.UploadListener;
import com.nu.art.modular.core.ModulesPack;
import com.nu.art.modular.tests.ModuleManager_TestClass;

import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Test_StorageBase
	extends ModuleManager_TestClass {

	private static final String outputPath = "build/test/output";
	private static final String resPath = "src/test/res";
	protected static FirebaseBucket bucket;

	static class Pack
		extends ModulesPack {

		Pack() {
			super(Module_FirebaseStorage.class);
		}

		@Override
		protected void init() {
			String home = System.getProperty("user.home");
			File credsFile = new File(home, "keys/dev-server-key.json");
			try {
				final GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credsFile));

				Module_FirebaseStorage module = getModule(Module_FirebaseStorage.class);
				module.DebugFlag.enable();
				module.setCredentialsGetter(new Getter<GoogleCredentials>() {
					@Override
					public GoogleCredentials get() {
						return credentials;
					}
				});
				module.connect();
			} catch (IOException e) {
				throw new BadImplementationException("unable to load credential file for storage test from: " + credsFile.getAbsolutePath());
			}
		}
	}

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUp() {
		initWithPacks(Pack.class);
		bucket = getModule(Module_FirebaseStorage.class).getOrCreateBucket("test-fcm-fdcdc.appspot.com");
		bucket.setUploadThreadCount(3);
		bucket.setDownloadThreadCount(3);
	}

	@Before
	public final void deleteOutput()
		throws IOException {
		FileTools.delete(new File(outputPath));
	}

	protected TestItem<Boolean> uploadFileTest(final String pathToLocal, final String mimeType, final String remotePath) {
		return createTest("UploadFile", "(" + mimeType + ")" + pathToLocal + " => " + remotePath)
			.setTimeout(40000)
			.setProcessor(new Processor<TestItem<Boolean>>() {
				@Override
				public void process(final TestItem<Boolean> test) {
					final File origin = new File(resPath, pathToLocal);
					final UploadTransaction transaction = bucket.createUploadTransaction(remotePath);
					transaction.setContentType(mimeType);
					transaction.setCompletionListener(new CompletionListener() {

						@Override
						public void onCompleted() {
							test._set(true);
						}
					});
					transaction.execute(new UploadListener() {
						@Override
						public void onUpload(OutputStream os, Throwable t)
							throws IOException {
							if (t != null) {
								test._set(t);
								logError("Error uploading file", t);
								return;
							}

							try {
								logInfo("Uploading...");
								StreamTools.copy(origin, os);
							} catch (IOException e) {
								test._set(e);
								throw e;
							}
						}
					});
				}
			});
	}

	protected TestItem<Boolean> downloadFileTest(final String pathToLocal, final String remotePath) {
		return createTest("DownloadFile", remotePath + " => " + pathToLocal)
			.setProcessor(new Processor<TestItem<Boolean>>() {
				@Override
				public void process(final TestItem<Boolean> test) {
					DownloadTransaction transaction = bucket.createDownloadTransaction(remotePath);
					transaction.setCompletionListener(new CompletionListener() {

						@Override
						public void onCompleted() {
							test._set(true);
						}
					});
					transaction.execute(new DownloadListener() {
						@Override
						public void onDownload(InputStream is, Throwable t)
							throws IOException {
							if (t != null) {
								test._set(t);
								logError("Error uploading file", t);
								return;
							}

							try {
								StreamTools.copy(is, new File(pathToLocal, "sample-image.jpg"));
							} catch (IOException e) {
								test._set(t);
								throw e;
							}
						}
					});
				}
			});
	}

	protected TestItem<Boolean> listFiles(final String remotePath, final String... expectedList) {
		return createTest("ListFile", remotePath)
			.setProcessor(new Processor<TestItem<Boolean>>() {
				@Override
				public void process(final TestItem<Boolean> test) {
					ListFiles transaction = bucket.listFiles(remotePath);
					transaction.setCompletionListener(new CompletionListener() {

						@Override
						public void onCompleted() {
							test._set(true);
						}
					});
					transaction.execute(new ListFilesListener() {
						@Override
						public void onReceivedFiles(RemoteFile[] files, Throwable t) {
							if (t != null) {
								test._set(t);
								logError("Error uploading file", t);
								return;
							}

							try {
								if (expectedList.length != files.length)
									throw new RuntimeException("Error: expectedList.length != files.length  <> " + expectedList.length + " != " + files.length);

								logInfo("Got files: " + Arrays.toString(files));
								for (RemoteFile file : files) {
									if (ArrayTools.contains(expectedList, file.name.replace(remotePath, "")))
										continue;

									throw new RuntimeException("Unexpected File: " + file.name);
								}
							} catch (RuntimeException e) {
								test._set(t);
								throw e;
							}
						}
					});
				}
			});
	}
}
