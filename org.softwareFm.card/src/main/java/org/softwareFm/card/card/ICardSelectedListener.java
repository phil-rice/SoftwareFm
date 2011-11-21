package org.softwareFm.card.card;

/** The user has clicked on this card */
public interface ICardSelectedListener {

	/** The user has clicked on this card */
	void cardSelected(String cardUrl);

	public static class Utils {

		public static ICardSelectedListener noListener() {
			return new ICardSelectedListener() {
				@Override
				public void cardSelected(String cardUrl) {
				}
			};
		}
	}
}
