package org.softwareFm.card.api;

public interface IAddItemProcessor {

	void addCollectionItem(RightClickCategoryResult rightClickCategoryResult);
	void addNewArtifact();

	public static class Utils {

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
