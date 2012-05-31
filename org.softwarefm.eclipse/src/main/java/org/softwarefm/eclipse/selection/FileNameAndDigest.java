package org.softwarefm.eclipse.selection;

import org.softwarefm.utilities.strings.Strings;

public class FileNameAndDigest {
	public String fileName;
	public String digest;

	public FileNameAndDigest(String fileName, String digest) {
		super();
		this.fileName = fileName;
		this.digest = digest;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((digest == null) ? 0 : digest.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
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
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FileNameAndDigest [fileName=" + fileName + ", digest=" + Strings.firstNCharacters(digest, 6) + "]";
	}

}
