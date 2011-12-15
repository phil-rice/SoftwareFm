package org.softwareFm.server.internal;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.softwareFm.server.GetResult;
import org.softwareFm.server.ILocalGit;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

public class LocalGit implements ILocalGit {

	private final File root;

	public LocalGit(File root) {
		this.root = root;
	}

	@Override
	public GetResult get(String url) {
		File directory = new File(root, url);
		File file = new File(directory, ServerConstants.dataFileName);
		if (!file.exists())
			return new GetResult(false, Collections.<String, Object> emptyMap());
		String text = Files.getText(file);
		Map<String, Object> result = new HashMap<String, Object>(Json.mapFromString(text));
		for (File child : Files.listChildDirectories(directory)) {
			Map<String, Object> collectionResults = Maps.newMap();
			for (File grandChild : Files.listChildDirectories(child)) {
				File grandChildFile = new File(grandChild, ServerConstants.dataFileName);
				if (grandChildFile.exists())
					collectionResults.put(grandChild.getName(), Json.mapFromString(Files.getText(grandChildFile)));
			}
			result.put(child.getName(), collectionResults);
		}
		return new GetResult(true, result);
	}

	@Override
	public void delete(String url) {
		File directory = new File(root, url);
		File file = new File(directory, ServerConstants.dataFileName);
		file.delete();
		directory.delete();//will only delete successfully is the directory is empty
	}

	@Override
	public void post(String url, Map<String, Object> map) {
		File directory = new File(root, url);
		directory.mkdirs();
		File file = new File(directory, ServerConstants.dataFileName);
		Files.setText(file, Json.toString(map));
	}

	@Override
	public String cloneOrPull(String url) {
		return null;
	}
}
