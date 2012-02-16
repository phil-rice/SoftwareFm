/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common;

import java.io.File;
import java.util.Map;

import org.softwareFm.common.collections.Files;
import org.softwareFm.common.internal.GitOperations;

public interface IGitOperations {
	void init(String url);

	void put(IFileDescription fileDescription, Map<String, Object> data);

	void pull(String url);

	void gc(String url);

	void addAllAndCommit(String url, String message);

	String getBranch(String url);

	void setConfigForRemotePull(String url, String remotePrefix);

	String getConfig(String url, String section, String subsection, String name);

	File getRoot();

	String getFileAsString(IFileDescription fileDescription);

	Map<String, Object> getFile(IFileDescription fileDescription);

	Map<String, Object> getFileAndDescendants(IFileDescription fileDescription);

	void clearCaches();

	abstract public static class Utils {
		public static IGitOperations gitOperations(File root) {
			return new GitOperations(root);
		}

		public static void init(IGitOperations gitOperations, File file) {
			String url = Files.offset(gitOperations.getRoot(), file);
			gitOperations.init(url);
		}

		public static IGitOperations noGitOperations() {
			return new IGitOperations() {

				@Override
				public void setConfigForRemotePull(String url, String remotePrefix) {
					throw new IllegalArgumentException();
				}

				@Override
				public void put(IFileDescription fileDescription, Map<String, Object> data) {
					throw new IllegalArgumentException();
				}

				@Override
				public void pull(String url) {
					throw new IllegalArgumentException();
				}

				@Override
				public void init(String url) {
					throw new IllegalArgumentException();
				}

				@Override
				public File getRoot() {
					throw new IllegalArgumentException();
				}

				@Override
				public Map<String, Object> getFileAndDescendants(IFileDescription fileDescription) {
					throw new IllegalArgumentException();
				}

				@Override
				public Map<String, Object> getFile(IFileDescription fileDescription) {
					throw new IllegalArgumentException();
				}

				@Override
				public String getConfig(String url, String section, String subsection, String name) {
					throw new IllegalArgumentException();
				}

				@Override
				public String getBranch(String url) {
					throw new IllegalArgumentException();
				}

				@Override
				public void gc(String url) {
					throw new IllegalArgumentException();
				}

				@Override
				public void clearCaches() {
					throw new IllegalArgumentException();
				}

				@Override
				public void addAllAndCommit(String url, String message) {
					throw new IllegalArgumentException();
				}

				@Override
				public String getFileAsString(IFileDescription fileDescription) {
					throw new IllegalArgumentException();
				}
			};
		}
	}
}