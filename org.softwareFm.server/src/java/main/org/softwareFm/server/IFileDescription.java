package org.softwareFm.server;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.internal.FileDescription;

public interface IFileDescription {

	File getDirectory(File root);

	File getFile(File root);

	File getFileInSubdirectory(File directory);

	File findRepositoryUrl(File root);

	String encode(Map<String, Object> data);

	Map<String, Object> decode(String text);

	public static class Utils {

		public static IFileDescription plain(String url) {
			return new FileDescription(url, ServerConstants.dataFileName, null);
		}

		public static IFileDescription encrypted(String url, String name, String key) {
			return new FileDescription(url, name, key);
		}

		public static IFileDescription fromRequest(RequestLine requestLine, Map<String, Object> parameters) {
			return plain(requestLine.getUri());
		}

		public static File findRepositoryUrl(File root, String url) {
			return plain(url).findRepositoryUrl(root);
		}

		public static IFileDescription plain(String url, String name) {
			return new FileDescription(url, name, null);
		}
	}

}
