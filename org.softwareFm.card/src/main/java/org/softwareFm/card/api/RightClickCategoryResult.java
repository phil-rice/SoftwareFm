package org.softwareFm.card.api;

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
		return itemType != Type.NOT_COLLECTION;
	}

	@Override
	public String toString() {
		return "RightClickCategoryResult [itemType=" + itemType + ", collectionName=" + collectionName + ", key=" + key + ", url=" + url + "]";
	}

	public String collectionUrl() {
		switch (itemType) {
		case ROOT_COLLECTION:
			return url ;
		case IS_COLLECTION:
			return url + "/" + key ;
		default:
			throw new IllegalStateException(itemType.name());
		}
	}

	public String itemUrl(String value) {
		return collectionUrl()+"/"+ value;
	}

}
