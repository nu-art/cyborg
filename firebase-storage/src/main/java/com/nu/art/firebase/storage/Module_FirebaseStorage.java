package com.nu.art.firebase.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BlobInfo.Builder;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.StorageOptions;
import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.exceptions.runtime.ImplementationMissingException;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.utils.PoolQueue;
import com.nu.art.modular.core.Module;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.HashMap;

public class Module_FirebaseStorage
	extends Module {

	public interface DownloadListener {

		void onDownload(InputStream is, Throwable t)
			throws IOException;
	}

	public interface UploadListener {

		void onUpload(OutputStream os, Throwable t)
			throws IOException;
	}

	public interface ListFilesListener {

		void onReceivedFiles(RemoteFile[] entities, Throwable t)
			throws IOException;
	}

	public interface CompletionListener {

		void onCompleted();
	}

	public static class RemoteFile {

		public String name;
		public boolean isFolder;
		public long size;

		public RemoteFile setName(String name) {
			this.name = name;
			return this;
		}

		public RemoteFile setIsFolder(boolean folder) {
			isFolder = folder;
			return this;
		}

		public RemoteFile setSize(long size) {
			this.size = size;
			return this;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public class FirebaseBucket {

		public class ListFiles
			extends Transaction<ListFiles, ListFilesListener> {

			private String relativePathInBucket;

			public ListFiles setRelativePathInBucket(String relativePathInBucket) {
				this.relativePathInBucket = relativePathInBucket;
				return this;
			}

			@Override
			ListFiles execute(ListFilesListener listener) {
				try {
					Iterable<Blob> filesAsBlobs = storage.get(bucketName).list(BlobListOption.prefix(relativePathInBucket)).getValues();
					ArrayList<RemoteFile> remoteFiles = new ArrayList<>();

					for (Blob blob : filesAsBlobs) {
						if (blob == null)
							continue;

						if (blob.getName().equals(relativePathInBucket + "/"))
							continue;

						remoteFiles.add(new RemoteFile().setIsFolder(blob.isDirectory()).setName(blob.getName()).setSize(blob.getSize()));
					}

					listener.onReceivedFiles(remoteFiles.toArray(new RemoteFile[0]), null);
				} catch (Throwable t) {
					try {
						listener.onReceivedFiles(new RemoteFile[0], t);
					} catch (Throwable e) {
						logError("Error while handling error", e);
					}
				} finally {
					// CLEAN UP ????
				}

				if (completionListener != null)
					completionListener.onCompleted();

				return this;
			}
		}

		public class UploadTransaction
			extends Transaction<UploadTransaction, UploadListener> {

			private String relativePathInBucket;
			private String contentType;
			private BlobTargetOption[] targetOptions = {};
			private UploadListener uploadListener;

			public UploadTransaction setRelativePathInBucket(String relativePathInBucket) {
				this.relativePathInBucket = relativePathInBucket;
				return this;
			}

			public final UploadTransaction setContentType(String contentType) {
				this.contentType = contentType;
				return this;
			}

			public final UploadTransaction execute(UploadListener listener) {
				if (listener == null)
					throw new ImplementationMissingException("MUST provide upload listener");

				this.uploadListener = listener;

				if (!uploadQueue.isAlive())
					throw new BadImplementationException("MUST initialize the bucket first");

				uploadQueue.addItem(this);
				return this;
			}

			private void execute() {
				WriteChannel writer = null;
				try {
					Builder builder = BlobInfo.newBuilder(bucketName, relativePathInBucket);
					if (contentType != null)
						builder.setContentType(contentType);

					BlobInfo blobInfo = builder.build();
					Blob blob = storage.create(blobInfo, targetOptions);
					writer = blob.writer();
					uploadListener.onUpload(Channels.newOutputStream(writer), null);
				} catch (Throwable t) {
					try {
						uploadListener.onUpload(null, t);
					} catch (Throwable e) {
						logError("Error while handling error", e);
					}
				} finally {
					if (writer != null) {
						try {
							writer.close();
						} catch (IOException ignore) {}
					}
				}
				if (completionListener != null)
					completionListener.onCompleted();
			}
		}

		@SuppressWarnings("unchecked")
		public abstract class Transaction<T extends Transaction, ListenerType> {

			CompletionListener completionListener;

			public final T setCompletionListener(CompletionListener completionListener) {
				this.completionListener = completionListener;
				return (T) this;
			}

			abstract T execute(ListenerType listener);
		}

		public class DownloadTransaction
			extends Transaction<DownloadTransaction, DownloadListener> {

			private String relativePathInBucket;
			private DownloadListener downloadListener;

			public DownloadTransaction setRelativePathInBucket(String relativePathInBucket) {
				this.relativePathInBucket = relativePathInBucket;
				return this;
			}

			public final DownloadTransaction execute(DownloadListener listener) {
				if (listener == null)
					throw new ImplementationMissingException("MUST provide download listener");

				if (!downloadQueue.isAlive())
					throw new BadImplementationException("MUST initialize the bucket first");

				this.downloadListener = listener;

				downloadQueue.addItem(this);
				return this;
			}

			private void execute() {
				ReadChannel reader = null;
				try {
					reader = storage.reader(bucketName, relativePathInBucket);
					downloadListener.onDownload(Channels.newInputStream(reader), null);
				} catch (Throwable e) {
					try {
						downloadListener.onDownload(null, e);
					} catch (Exception e1) {
						logError("Error while handling error", e);
					}
				} finally {
					if (reader != null)
						reader.close();
				}
				if (completionListener != null)
					completionListener.onCompleted();
			}
		}

		private final String bucketName;

		private PoolQueue<UploadTransaction> uploadQueue = new PoolQueue<UploadTransaction>() {
			@Override
			protected void executeAction(UploadTransaction transaction) {
				transaction.execute();
			}
		};

		private PoolQueue<DownloadTransaction> downloadQueue = new PoolQueue<DownloadTransaction>() {
			@Override
			protected void executeAction(DownloadTransaction transaction) {
				transaction.execute();
			}
		};

		public FirebaseBucket(String bucketName) {
			this.bucketName = bucketName;
		}

		public FirebaseBucket setUploadThreadCount(int uploadThreadCount) {
			uploadQueue.createThreads("bucket-upload-" + bucketName + "", uploadThreadCount);
			return this;
		}

		public FirebaseBucket setDownloadThreadCount(int downloadThreadCount) {
			downloadQueue.createThreads("bucket-upload-" + bucketName + "", downloadThreadCount);
			return this;
		}

		public final ListFiles listFiles(String relativePathInBucket) {
			return new ListFiles().setRelativePathInBucket(relativePathInBucket);
		}

		public final UploadTransaction createUploadTransaction(String relativePathInBucket) {
			return new UploadTransaction().setRelativePathInBucket(relativePathInBucket);
		}

		public final DownloadTransaction createDownloadTransaction(String relativePathInBucket) {
			return new DownloadTransaction().setRelativePathInBucket(relativePathInBucket);
		}
	}

	private Storage storage;
	private HashMap<String, FirebaseBucket> buckets = new HashMap<>();
	private Getter<GoogleCredentials> credentialsGetter;

	public void setCredentialsGetter(Getter<GoogleCredentials> credentialsGetter) {
		this.credentialsGetter = credentialsGetter;
	}

	public void connect() {
		try {
			GoogleCredentials credentials = credentialsGetter.get();
			storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
		} catch (Throwable e) {
			throw new BadImplementationException("Unable to stare storage", e);
		}
	}

	@Override
	protected void init() {
	}

	public final FirebaseBucket getOrCreateBucket(String name) {
		FirebaseBucket firebaseBucket = buckets.get(name);
		if (firebaseBucket != null)
			return firebaseBucket;

		buckets.put(name, firebaseBucket = new FirebaseBucket(name));
		return firebaseBucket;
	}
}
