/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.modifiers.internal;

import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.modifiers.ICardDataModifier;

public class HideColectionsModifier implements ICardDataModifier {

	@Override
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData) {
		Map<String, Object> result = Maps.copyMap(rawData);
		for (Entry<String, Object> entry : rawData.entrySet())
			if (entry.getValue() instanceof Map<?, ?>)
				result.remove(entry.getKey());
		return result;
	}

}