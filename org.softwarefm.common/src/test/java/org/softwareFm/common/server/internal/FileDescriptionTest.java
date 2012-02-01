package org.softwareFm.common.server.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.internal.FileDescription;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.server.GitTest;
import org.softwareFm.common.url.Urls;

public class FileDescriptionTest extends GitTest {

	private FileDescription fileDescription;
	private String url;
	private String name;
	private String key;

	public void testGetDirectory() {
		File directory = fileDescription.getDirectory(root);
		assertEquals(new File(root, url), directory);
	}

	public void testGetFile() {
		File file = fileDescription.getFile(root);
		assertEquals(new File(root, Urls.compose(url, name)), file);
	}

	public void testFindRepositoryUrl() {
		assertNull(fileDescription.findRepositoryUrl(localRoot));
		localOperations.init("repo");
		assertEquals(new File(localRoot, "repo"), fileDescription.findRepositoryUrl(localRoot));
	}

	public void testEncodeDecodeWithKey() {
		Map<String, Object> map = Maps.stringObjectMap("a", 1l);
		String encoded = Crypto.aesEncrypt(key, Json.toString(map));
		assertEquals(encoded, fileDescription.encode(map));
		Map<String, Object> value = fileDescription.decode(encoded);
		assertEquals(map, value);
	}

	public void testEncodeDecodeWithoutKey() {
		fileDescription = (FileDescription) IFileDescription.Utils.plain(url);
		Map<String, Object> map = Maps.stringObjectMap("a", 1l);
		assertEquals(Json.toString(map), fileDescription.encode(map));
		Map<String, Object> value = fileDescription.decode(Json.toString(map));
		assertEquals(map, value);
	}

	public void testGetFileInSubDirectory() {
		File someFile = new File("someFile");
		assertEquals(new File(someFile, name), fileDescription.getFileInSubdirectory(someFile));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		url = "repo/url";
		name = "name";
		key = Crypto.makeKey();
		fileDescription = new FileDescription(url, name, key);
	}

}
