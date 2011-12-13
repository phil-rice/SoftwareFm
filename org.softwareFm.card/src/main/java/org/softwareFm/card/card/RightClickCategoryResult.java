/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card;

/** Categorised data about the line that has just been right clicked on in the card */
public class RightClickCategoryResult {

	public static enum Type {
		/** Right clicked on an item that doesn't represent a collection */
		NOT_COLLECTION,
		/** Right clicked on empty space in a folder */
		ROOT_FOLDER,
		/** Right clicked on an item in a collection. (The card has sling:resourceType=collection) */
		ROOT_COLLECTION,
		/** Right clicked on an item that represents a collection (for example mailingList,artifact,version) */
		IS_COLLECTION,
		/** Right clicked: no item, no cardtype. This is probably an empty collection that hasn't been created yet */
		COLLECTION_NOT_CREATED_YET,
		/** Right clicked on an item that reprsents all the child folders */
		IS_FOLDERS;
	}

	public final Type itemType;
	/** if ROOT_FOLDER/IS_FOLDERS: folder, if ROOT_COLLECTION: lastSegmentOfUrl, if IS_COLLECTION: name of the key */
	public final String collectionName;
	/** The key that was right clicked on */
	public final String key;
	/** The url used while categorising */
	public final String url;

	public RightClickCategoryResult(Type itemType, String collectionName, String key, String url) {
		super();
		this.itemType = itemType;
		this.collectionName = collectionName;
		this.key = key;
		this.url = url;
	}

	public boolean isCollection() {
		return itemType != Type.NOT_COLLECTION ;
	}

	@Override
	public String toString() {
		return "RightClickCategoryResult [itemType=" + itemType + ", collectionName=" + collectionName + ", key=" + key + ", url=" + url + "]";
	}

	public String collectionUrl() {
		switch (itemType) {
		case COLLECTION_NOT_CREATED_YET:
			return url;
		case ROOT_COLLECTION:
			return url;
		case IS_FOLDERS:
			return url;
		case IS_COLLECTION:
			return url + "/" + key;
		default:
			throw new IllegalStateException(itemType.name());
		}
	}

	public String itemUrl(String value) {
		String result = collectionUrl() + "/" + value;
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collectionName == null) ? 0 : collectionName.hashCode());
		result = prime * result + ((itemType == null) ? 0 : itemType.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		RightClickCategoryResult other = (RightClickCategoryResult) obj;
		if (collectionName == null) {
			if (other.collectionName != null)
				return false;
		} else if (!collectionName.equals(other.collectionName))
			return false;
		if (itemType != other.itemType)
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

}