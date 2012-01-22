package org.softwareFm.server;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.internal.PlainFileDescription;

public interface IFileDescription {

	File getDirectory(File root);

	File findRepositoryUrl(File root);

	public static class Utils {

		public static IFileDescription fromRequest(RequestLine requestLine, Map<String, Object> parameters) {
			return plain(requestLine.getUri());
		}

		public static IFileDescription plain(String url) {
			return new PlainFileDescription(url);
		}

		public static File findRepositoryUrl(File root, String url) {
			return plain(url).findRepositoryUrl(root);
		}
	}
}
