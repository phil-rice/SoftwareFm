package org.softwareFm.server;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.internal.FileDescription;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.url.Urls;

public interface IFileDescription {

	File getDirectory(File root);

	File getFile(File root);

	File getFileInSubdirectory(File directory);

	File findRepositoryUrl(File root);

	String url();

	String encode(Map<String, Object> data);

	Map<String, Object> decode(String text);

	public static class Utils {

		public static IFileDescription plain(String url) {
			return new FileDescription(url, CommonConstants.dataFileName, null);
		}

		public static IFileDescription encrypted(String url, String name, String key) {
			return new FileDescription(url, name, key);
		}

		public static IFileDescription fromRequest(RequestLine requestLine, Map<String, Object> parameters) {
			return plain(requestLine.getUri());
		}

		public static File findRepositoryFile(File root, String url) {
			return plain(url).findRepositoryUrl(root);
		}

		public static String findRepositoryUrl(File root, String url) {
			File file = plain(url).findRepositoryUrl(root);
			if (file == null)
				return null;
			String result = Files.offset(root, file);
			return result;
		}

		public static IFileDescription plain(String url, String name) {
			return new FileDescription(url, name, null);
		}

		public static void save(File root, IFileDescription fileDescription, Map<String, Object> data) {
			File file = fileDescription.getFile(root);
			String string = fileDescription.encode(data);
			file.getParentFile().mkdirs();
			Files.setText(file, string);
		}

		public static Map<String, Object> load(File root, IFileDescription fileDescription) {
			File file = fileDescription.getFile(root);
			String text = Files.getText(file);
			Map<String, Object> result = fileDescription.decode(text);
			return result;
		}

		public static void merge(IGitOperations gitOperations, IFileDescription fileDescription, Map<String, Object> toMerge) {
			Map<String, Object> initialData = gitOperations.getFile(fileDescription);
			@SuppressWarnings("unchecked")
			Map<String, Object> map = Maps.merge(initialData, toMerge);
			gitOperations.put(fileDescription, map);
		}

		public static void addAllAndCommit(IGitOperations gitOperations, IFileDescription fileDescription, String message) {
			File root = gitOperations.getRoot();
			File repositoryUrl = fileDescription.findRepositoryUrl(root);
			String url = Files.offset(root, repositoryUrl);
			gitOperations.addAllAndCommit(url, message);

		}

		public static IFileDescription compose(String... strings) {
			return IFileDescription.Utils.plain(Urls.compose(strings));
		}

	}

}
