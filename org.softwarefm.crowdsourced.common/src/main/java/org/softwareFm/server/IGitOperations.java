package org.softwareFm.server;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.internal.GitOperations;
import org.softwareFm.utilities.collections.Files;

public interface IGitOperations {

	void init(String url);

	void pull(String url);

	void gc(String url);

	void addAllAndCommit(String url, String message);

	String getBranch(String url);

	Map<String, Object> getFile(IFileDescription fileDescription);

	Map<String, Object> getFileAndDescendants(IFileDescription fileDescription);

	void put(IFileDescription fileDescription, Map<String,Object> data);
	
	void setConfigForRemotePull(String url, String remotePrefix);

	String getConfig(String url, String section, String subsection, String name);
	
	File getRoot();
	
	public static class Utils{
		public static IGitOperations gitOperations(File root){
			return new GitOperations(root);
		}
		
		public static void init(IGitOperations gitOperations, File file){
			String url = Files.offset(gitOperations.getRoot(), file);
			gitOperations.init(url);
		}
	}
}
