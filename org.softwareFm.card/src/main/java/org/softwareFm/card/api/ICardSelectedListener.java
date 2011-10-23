package org.softwareFm.card.api;

public interface ICardSelectedListener {

	void cardSelected(ICard card);

	public static class Utils {

		public static ICardSelectedListener noListener() {
			return new ICardSelectedListener() {
				@Override
				public void cardSelected(ICard card) {
				}
			};
		}
	}
}
