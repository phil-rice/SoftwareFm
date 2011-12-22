package org.softwareFm.server.processors;

import java.io.File;
import java.io.InputStream;

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
	}
}
