package org.softwarefm.eclipse.selection;

import java.io.File;

import org.softwarefm.utilities.strings.Strings;

public class FileNameAndDigest {
	public String digest;
	public final File file;

	public FileNameAndDigest(File file, String digest) {
		super();
		this.digest = digest;
		this.file = file;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((digest == null) ? 0 : digest.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
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
		FileNameAndDigest other = (FileNameAndDigest) obj;
		if (digest == null) {
			if (other.digest != null)
				return false;
		} else if (!digest.equals(other.digest))
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FileNameAndDigest [digest=" + digest + ", file=" + Strings.firstNCharacters(digest, 6) + "]";
	}

}
