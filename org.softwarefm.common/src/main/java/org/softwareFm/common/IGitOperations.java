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
			};
		}
	}
}
