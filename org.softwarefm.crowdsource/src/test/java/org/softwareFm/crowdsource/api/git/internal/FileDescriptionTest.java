/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.git.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.crowdsource.api.git.GitTest;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.git.internal.FileDescription;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.Urls;

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