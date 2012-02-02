/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import java.util.Map;

import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.modifiers.ICardDataModifier;

public class MockKeyValueModifier implements ICardDataModifier {
	public Map<String, Object> result;
	public Map<String, Object> rawData;
	public String url;

	public MockKeyValueModifier(Map<String, Object> result) {
		this.result = result;
	}

	@Override
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData) {
		this.url = url;
		this.rawData = rawData;
		return result;
	}

}