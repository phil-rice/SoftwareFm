/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card;

import java.util.Map;

import org.softwareFm.card.configuration.CardConfig;

public interface ICardData extends IHasCardConfig {

	/** Typically this is determined by the sling:resourceType in the rawdata. May be null */
	String cardType();

	/** This is called when more information has been found about the card. The newValue should replace any existing data about this key. The key should already be present in the data */
	void valueChanged(String key, Object newValue);

	/** What url on the server does this card represent? */
	String url();

	/** A copy of the data that the card is displaying: thread safe, and changes to this map have no impact */
	Map<String, Object> data();

	static class Utils {
		public static ICardData create(final CardConfig cardConfig, final String cardType, final String url, final Map<String, Object> data) {
			return new ICardData() {
				@Override
				public CardConfig getCardConfig() {
					return cardConfig;
				}

				@Override
				public String cardType() {
					return cardType;
				}

				@Override
				public void valueChanged(String key, Object newValue) {
					throw new UnsupportedOperationException();
				}

				@Override
				public String url() {
					return url;
				}

				@Override
				public Map<String, Object> data() {
					return data;
				}
			};
		}
	}

}