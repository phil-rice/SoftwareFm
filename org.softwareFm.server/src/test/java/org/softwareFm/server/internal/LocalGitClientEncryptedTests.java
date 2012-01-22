package org.softwareFm.server.internal;

import java.util.Map;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

public class LocalGitClientEncryptedTests extends GitTest {

	private LocalGitClient client;

	public void testLocalGetWithEncryptedData() {
		String key = Crypto.makeKey();
		IFileDescription fileDescription = IFileDescription.Utils.encrypted("a/b/c", "name1", key);
		put(localRoot, fileDescription, v31);
		put(localRoot, IFileDescription.Utils.encrypted("a/b/c/d", "name1", key), v32);
		put(localRoot, IFileDescription.Utils.encrypted("a/b/c/d/e", "name1", key), v41);
		put(localRoot, IFileDescription.Utils.encrypted("a/b/c/f", "name1", key), v42);

		assertEquals(v31, client.getFile(fileDescription).data);

		checkLocalGet(client, fileDescription, Maps.with(v31, "d", Maps.with(v32, "e", v41), "f", v42));

		Map<String, Object> actual = Json.mapFromString(Crypto.aesDecrypt(key, Files.getText(fileDescription.getFile(localRoot))));
		assertEquals(v31, actual);
	}
	
	public void testWithEncryptedData(){
		String key = Crypto.makeKey();
		IFileDescription fileDescription = IFileDescription.Utils.encrypted("a/b/c", "name1", key);
		put(localRoot, fileDescription, v31);
		
		checkGetFile(client, fileDescription, v31);
	}

	public void testGet() {
		put(localRoot, "a/b/c", v11);
		put(localRoot, IFileDescription.Utils.plain("a/b/c", "name1"), v12);
		put(localRoot, IFileDescription.Utils.plain("a/b/c", "name2"), v21);

		assertEquals(v11, client.getFile(IFileDescription.Utils.plain("a/b/c")).data);
		assertEquals(v12, client.getFile(IFileDescription.Utils.plain("a/b/c", "name1")).data);
		assertEquals(v21, client.getFile(IFileDescription.Utils.plain("a/b/c", "name2")).data);

		assertEquals(v11, client.localGet(IFileDescription.Utils.plain("a/b/c")).data);
		assertEquals(v12, client.localGet(IFileDescription.Utils.plain("a/b/c", "name1")).data);
		assertEquals(v21, client.localGet(IFileDescription.Utils.plain("a/b/c", "name2")).data);
	}

	public void testLocalGetReturnsDataFileModifiedByDataFilesInSubDirectory() {
		put(localRoot, "a/b/c", v11);
		put(localRoot, "a/b/c/d", v12);
		put(localRoot, "a/b/c/d/e", v21);
		put(localRoot, "a/b/c/f", v22);

		put(localRoot, IFileDescription.Utils.plain("a/b/c", "name1"), v31);
		put(localRoot, IFileDescription.Utils.plain("a/b/c/d", "name1"), v32);
		put(localRoot, IFileDescription.Utils.plain("a/b/c/d/e", "name1"), v41);
		put(localRoot, IFileDescription.Utils.plain("a/b/c/f", "name1"), v42);

		assertEquals(v11, client.getFile(IFileDescription.Utils.plain("a/b/c")).data);
		assertEquals(v31, client.getFile(IFileDescription.Utils.plain("a/b/c", "name1")).data);

		checkLocalGet(client, "a/b/c", Maps.with(v11, "d", Maps.with(v12, "e", v21), "f", v22));
		checkLocalGet(client, IFileDescription.Utils.plain("a/b/c", "name1"), Maps.with(v31, "d", Maps.with(v32, "e", v41), "f", v42));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		client = new LocalGitClient(localRoot);
	}

}
