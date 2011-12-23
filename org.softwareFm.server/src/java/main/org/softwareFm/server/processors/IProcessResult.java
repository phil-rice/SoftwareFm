package org.softwareFm.server.processors;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.softwareFm.utilities.collections.Files;

public interface IProcessResult {

	void process(HttpResponse response) throws Exception;

	public static class Utils {
		public static IProcessResult processString(final String string) {
			return new IProcessResult() {
				@Override
				public void process(HttpResponse response) throws Exception {
					response.setEntity(new StringEntity(string));
				}
			};
		}

		public static IProcessResult doNothing() {
			return new IProcessResult() {
				@Override
				public void process(HttpResponse response) throws Exception {
				}
			};
		}

		public static IProcessResult processFile(final File file) {
			return new IProcessResult() {
				@Override
				public void process(HttpResponse response) throws Exception {
					String contentType = Files.defaultMimeType(file.getName());
					String format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(file.lastModified());
					response.setHeader("Last-Modified", format);//Last-Modified:Wed, 14 Dec 2011 19:57:26 GMT
					response.setEntity(new FileEntity(file, contentType));
				}
			};
		}
		public static IProcessResult processStream(final InputStream inputStream, final String contentType) {
			return new IProcessResult() {
				@Override
				public void process(HttpResponse response) throws Exception {
					response.setEntity(new InputStreamEntity(inputStream, -1));
					response.setHeader("Content-Type", contentType);
				}
			};
		}

		public static IProcessResult processError(final int statusCode, final String message) {
			return new IProcessResult() {
				@Override
				public void process(HttpResponse response) throws Exception {
					response.setStatusCode(statusCode);
					response.setReasonPhrase("Not Found");
					response.setEntity(new StringEntity(message));
				}
			};
		}
	}
}
