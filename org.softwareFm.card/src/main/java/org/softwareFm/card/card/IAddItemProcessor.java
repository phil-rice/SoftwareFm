package org.softwareFm.card.card;

/** When the user expresses through menu or otherwise that they wish to add an item to a collection this is invoked. In the softwarefm application, this pops up a gui requesting data about the item to be added, then adds it */
public interface IAddItemProcessor {

	/** The user has said 'add another item to "this" collection' */
	void addCollectionItem(RightClickCategoryResult rightClickCategoryResult);

	// TODO addNewArtifact should be removed from this interface, as it is specific to softwarefm
	void addNewArtifact();

	public static class Utils {

		/** An IAddItemProcessor that does nothing */
		public static IAddItemProcessor noAddItemProcessor() {
			return new IAddItemProcessor() {
				@Override
				public void addCollectionItem(RightClickCategoryResult rightClickCategoryResult) {
				}

				@Override
				public void addNewArtifact() {
				}
			};
		}
	}
}
