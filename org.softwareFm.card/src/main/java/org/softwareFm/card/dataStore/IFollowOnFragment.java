package org.softwareFm.card.dataStore;

import java.util.Map;

/** Called for each entry in a map returned from the server as a card. If it returns a non null, then a request for more data is made */
public interface IFollowOnFragment {

	/** Called for each entry in a map returned from the server as a card. If it returns a non null, then a request for more data is made */
	String findFollowOnFragment(String key, Object value);

	public static class Utils {
		public static IFollowOnFragment followOnMaps = new IFollowOnFragment() {
			@Override
			public String findFollowOnFragment(String key, Object value) {
				if (value instanceof Map<?, ?>)
					return key;
				else
					return null;
			}
		};

	}
}
