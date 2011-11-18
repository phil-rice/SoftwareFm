package org.softwareFm.card.api;

public interface IAddItemProcessor {


	void process(RightClickCategoryResult rightClickCategoryResult);

	public static class Utils {

		public static IAddItemProcessor noAddItemProcessor() {
			return new IAddItemProcessor() {
				@Override
				public void process(RightClickCategoryResult rightClickCategoryResult) {
				}
			};
		}
	}
}
