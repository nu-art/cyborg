package com.nu.art.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class FileResponseListener<ErrorType>
	extends HttpResponseListener<InputStream, ErrorType> {

	private final File targetFile;

	protected FileResponseListener(File targetFile, Class<ErrorType> errorType) {
		super(InputStream.class, errorType);
		this.targetFile = targetFile;
	}

	@Override
	public final void onSuccess(HttpResponse httpResponse, InputStream inputStream) {
		FileOutputStream outputStream = null;
		try {
			List<String> header = httpResponse.getHeader("content-length");
			int available;
			if (header.size() > 0)
				available = Integer.parseInt(header.get(0));
			else
				available = inputStream.available();

			outputStream = new FileOutputStream(targetFile);
			byte[] buffer = new byte[1024];
			int length;
			long cached = 0;
			long downloaded = 0;

			while ((length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
				cached += length;
				downloaded += length;
				onDownloadProgress(downloaded, available);
				if (cached >= 1024 * 1024) {
					outputStream.flush();
					cached = 0;
				}
			}
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException ignore) {
				}
			}
		}
		onDownloadCompleted();
	}

	protected abstract void onDownloadCompleted();
}
