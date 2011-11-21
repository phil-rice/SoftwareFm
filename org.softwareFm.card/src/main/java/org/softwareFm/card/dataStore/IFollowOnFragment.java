package org.softwareFm.card.dataStore;

/** Called for each entry in a map returned from the server as a card. If it returns a non null, then a request for more data is made */
public interface IFollowOnFragment {

	/** Called for each entry in a map returned from the server as a card. If it returns a non null, then a request for more data is made */
	String findFollowOnFragment(String key, Object value);
}
