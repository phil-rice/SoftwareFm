package org.softwareFm.card.api;

import java.util.Map;

import org.softwareFm.card.api.RightClickCategoryResult.Type;

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
