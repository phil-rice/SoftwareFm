package org.softwareFm.eclipse.actions.internal;

import java.util.Map;

import org.softwareFm.swt.card.ICard;

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
