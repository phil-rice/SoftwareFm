/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.git.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.json.Json;

public class FileDescription implements IFileDescription {

	private final String url;
	private final String name;
	private final String key;

	public FileDescription(String url, String name, String key) {
		this.url = url;
		this.name = name;
		this.key = key;
		if (url == null)
			throw new NullPointerException(toString());
	}

	@Override
	public String name() {
		return name;
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
		try {
			String raw = key == null ? text : Crypto.aesDecrypt(key, text);
			return Json.mapFromString(raw);
		} catch (Exception e) {
			throw new RuntimeException("raw was [" + text +"]", e);
		}
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileDescription other = (FileDescription) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

}