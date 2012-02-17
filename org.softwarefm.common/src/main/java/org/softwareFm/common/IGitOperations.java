/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.collections.Files;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.internal.GitOperations;

public interface IGitOperations {
	/** Creates a repository at this url */
	void init(String url);

	/** creates/overwrites a file at this file description */
	void put(IFileDescription fileDescription, Map<String, Object> data);

	/** Assumes the file is a list of maps, each map is on a separate line (separated by \n). If encrypted, each line is separately encrypted */
	void append(IFileDescription fileDescription, Map<String, Object> data);

	/**
	 * Assumes the file is a list of maps, each map is on a separate line (separated by \n). If encrypted, each line is separately encrypted <br />
	 * Each line that is accepted is modified by the transformation<br />
	 * Lines that are not accepted are unchanged, so that git can find deltas easily Commits at the end if needed
	 * @return TODO
	 */
	int map(IFileDescription fileDescription, IFunction1<Map<String, Object>, Boolean> acceptor, IFunction1<Map<String, Object>, Map<String, Object>> transform, String commitMessage);

	/** Pull from the remote repository. */
	void pull(String url);

	/** Garbage collect the repository at this url */
	void gc(String url);

	/** Add any changed files to staging, then commit all the changes */
	void addAllAndCommit(String url, String message);

	/** Find the name of the current branch */
	String getBranch(String url);

	/** Make it so that pulls come from the remotePrefix. */
	void setConfigForRemotePull(String url, String remotePrefix);

	/** get the config from the repository at url */
	String getConfig(String url, String section, String subsection, String name);

	/** Where is the root of this tree of repositories located */
	File getRoot();

	/** loads a file as a string */
	String getFileAsString(IFileDescription fileDescription);

	/** loads a file as a map */
	Map<String, Object> getFile(IFileDescription fileDescription);

	/** loads a file as a list of maps. Each line is a map, and optionally encrypted */
	List<Map<String, Object>> getFileAsListOfMaps(IFileDescription fileDescription);

	/** creates a map which is the aggregate of the file and it's descendants. The descendants are in sub directories with the same crypto key and name */
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

				@Override
				public void append(IFileDescription fileDescription, Map<String, Object> data) {
					throw new IllegalArgumentException();
				}

				@Override
				public List<Map<String, Object>> getFileAsListOfMaps(IFileDescription fileDescription) {
					throw new IllegalArgumentException();
				}

				@Override
				public int map(IFileDescription fileDescription, IFunction1<Map<String, Object>, Boolean> acceptor, IFunction1<Map<String, Object>, Map<String, Object>> transform, String commitMessage) {
					throw new IllegalArgumentException();
				}
			};
		}
	}
}