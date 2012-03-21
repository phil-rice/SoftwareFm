/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.git;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.crowdsource.git.internal.FileDescription;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.Urls;

public interface IFileDescription {

	File getDirectory(File root);

	File getFile(File root);

	File getFileInSubdirectory(File directory);

	File findRepositoryUrl(File root);

	String url();
	
	String crypto();
	
	String name();

	String encode(Map<String, Object> data);

	Map<String, Object> decode(String text);

	abstract public static class Utils {

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