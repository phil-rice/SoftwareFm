package org.softwareFm.server.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.softwareFm.server.GetResult;
import org.softwareFm.server.ILocalGitClient;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

public class LocalGitClient implements ILocalGitClient {

	protected final File root;

	public LocalGitClient(File root) {
		this.root = root;
	}

	@Override
	public GetResult getFile(String url) {
		File directory = new File(root, url);
		if (!directory.exists())
			return GetResult.create(null);
		File file = new File(directory, ServerConstants.dataFileName);
		Map<String, Object> result = file.exists() ? new HashMap<String, Object>(Json.mapFromString(Files.getText(file))) : Maps.<String, Object> newMap();
		return GetResult.create(result);
	}

	@Override
	public GetResult localGet(String url) {
		GetResult rawFile = getFile(url);
		if (!rawFile.found)
			return rawFile;
		File directory = new File(root, url);
		Map<String, Object> result = rawFile.data;
		for (File child : Files.listChildDirectories(directory)) {
			File childFile = new File(child, ServerConstants.dataFileName);
			Map<String, Object> collectionResults = Maps.newMap();
			if (childFile.exists())
				collectionResults.putAll(Json.mapFromString(Files.getText(childFile)));
			for (File grandChild : Files.listChildDirectories(child))
				addDataFromFileIfExists(collectionResults, grandChild);
			result.put(child.getName(), collectionResults);
		}
		return new GetResult(true, result);
	}

	private void addDataFromFileIfExists(Map<String, Object> collectionResults, File directory) {
		File file = new File(directory, ServerConstants.dataFileName);
		if (file.exists())
			collectionResults.put(directory.getName(), Json.mapFromString(Files.getText(file)));
	}

	@Override
	public void delete(String url) {
		File directory = new File(root, url);
		File file = new File(directory, ServerConstants.dataFileName);
		file.delete();
		directory.delete();// will only delete successfully is the directory is empty
	}

	@Override
	public void post(String url, Map<String, Object> map) {
		File directory = new File(root, url);
		directory.mkdirs();
		File file = new File(directory, ServerConstants.dataFileName);
		Files.setText(file, Json.toString(map));
	}

	@Override
	public File getRoot() {
		return root;
	}

}
