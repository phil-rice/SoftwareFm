package org.softwareFm.collections.actions.internal;

import java.util.Map;

import org.softwareFm.card.card.ICard;

public interface IAfterDisplayCard {

	void process(ICard card, Map<String, Object> groupArtifactVersionMap);
	static class Utils {

		public static IAfterDisplayCard noCallback() {
			return new IAfterDisplayCard() {

				@Override
				public void process(ICard card, Map<String, Object> groupArtifactVersionMap) {
				}
			};
		}
		
	}


}
