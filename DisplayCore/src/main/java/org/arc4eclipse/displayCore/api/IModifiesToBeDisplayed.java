package org.arc4eclipse.displayCore.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface IModifiesToBeDisplayed {
	List<NameSpaceNameAndValue> add(Map<String, Object> data, Map<String, Object> context);

	public static class Utils {
		public static IModifiesToBeDisplayed addAllways(final String key, final Object value) {
			return new IModifiesToBeDisplayed() {
				@Override
				public List<NameSpaceNameAndValue> add(Map<String, Object> data, Map<String, Object> context) {
					return Arrays.asList(NameSpaceNameAndValue.Utils.make(key, value));
				}
			};

		}
	}
}
