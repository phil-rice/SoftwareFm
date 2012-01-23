package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.GetResult;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.ILocalGitClient;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.maps.Maps;

public class LocalGitClient implements ILocalGitClient {

	protected final File root;

	public LocalGitClient(File root) {
		this.root = root;
	}

	@Override
	public GetResult getFile(IFileDescription fileDescription) {
		File directory = fileDescription.getDirectory(root);
		if (!directory.exists())
			return GetResult.notFound();
		File file = fileDescription.getFile(root);
		if (file.exists()){
			String text = Files.getText(file);
			Map<String, Object> map = fileDescription.decode(text);
			return GetResult.create(map);
			
		}else
			return GetResult.create(Maps.newMap());
	}

	@Override
	public GetResult localGet(IFileDescription fileDescription) {
		GetResult rawFile = getFile(fileDescription);
		if (!rawFile.found)
			return rawFile;
		File directory = fileDescription.getDirectory(root);
		Map<String, Object> result = rawFile.data;
		for (File child : Files.listChildDirectoriesIgnoringDot(directory)) {
			File childFile = fileDescription.getFileInSubdirectory(child);
			Map<String, Object> collectionResults = Maps.newMap();
			if (childFile.exists())
				collectionResults.putAll(fileDescription.decode(Files.getText(childFile)));
			for (File grandChild : Files.listChildDirectoriesIgnoringDot(child))
				addDataFromFileIfExists(fileDescription, collectionResults, grandChild);
			result.put(child.getName(), collectionResults);
		}
		return new GetResult(true, result);
	}

	private void addDataFromFileIfExists(IFileDescription fileDescription, Map<String, Object> collectionResults, File directory) {
		File file = fileDescription.getFileInSubdirectory(directory);
		if (file.exists())
			collectionResults.put(directory.getName(), fileDescription.decode(Files.getText(file)));
	}

	@Override
	public void delete(IFileDescription fileDescription) {
		File directory = fileDescription.getDirectory(root);
		File file = fileDescription.getFile(root);
		file.delete();
		directory.delete();// will only delete successfully if the directory is empty
	}

	@Override
	public void post(IFileDescription fileDescription, Map<String, Object> map) {
		File directory = fileDescription.getDirectory(root);
		directory.mkdirs();
		File file = fileDescription.getFile(root);
		String text = fileDescription.encode(map);
		
		Files.setText(file, text);
	}

	@Override
	public File getRoot() {
		return root;
	}

}
