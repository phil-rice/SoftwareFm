package org.softwareFm.card.api;

import java.util.Map;

import org.softwareFm.card.api.RightClickCategoryResult.Type;

/** Determines information about the item that has just been right clicked on in the card */
public interface IRightClickCategoriser {

	RightClickCategoryResult categorise(String url, Map<String, Object> map, String key);

	public static class Utils {

		public static IRightClickCategoriser noRightClickCategoriser() {
			return new IRightClickCategoriser() {

				@Override
				public RightClickCategoryResult categorise(String url, Map<String, Object> map, String key) {
					return new RightClickCategoryResult(Type.NOT_COLLECTION, null, key, url);
				}
			};
		}

	}

}
