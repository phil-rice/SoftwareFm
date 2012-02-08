package org.softwareFm.common.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.json.Json;

public class FileDescription implements IFileDescription {

	private final String url;
	private final String name;
	private final String key;

	public FileDescription(String url, String name, String key) {
		this.url = url;
		this.name = name;
		this.key = key;
	}

	@Override
	public File getFileInSubdirectory(File directory) {
		return new File(directory, name);
	}

	@Override
	public String crypto() {
		return key;
	}

	@Override
	public File getFile(File root) {
		return new File(getDirectory(root), name);
	}

	@Override
	public File getDirectory(File root) {
		return new File(root, url);
	}

	@Override
	public String encode(Map<String, Object> data) {
		String raw = Json.toString(data);
		if (key == null)
			return raw;
		else
			return Crypto.aesEncrypt(key, raw);
	}

	@Override
	public Map<String, Object> decode(String text) {
		String raw = key == null ? text : Crypto.aesDecrypt(key, text);
		return Json.mapFromString(raw);
	}

	@Override
	public File findRepositoryUrl(File root) {
		final File dir = new File(root, url);
		for (File file : Files.listParentsUntil(root, dir))
			if (new File(file, CommonConstants.DOT_GIT).exists())// found it
				return file;
		return null;
	}

	@Override
	public String toString() {
		return "FileDescription [url=" + url + ", name=" + name + ", key=" + key + "]";
	}

	@Override
	public String url() {
		return url;
	}

}
