/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.internal;

import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.constants.CardConstants;

public class SoftwareFmDefaultChildFunction implements IFunction1<ICardData, String> {
	final Map<String, String> typeToDefaultChildMap = Maps.makeMap(//
			CardConstants.group, CardConstants.artifact,//
			CardConstants.artifact, JarAndPathConstants.version,//
			JarAndPathConstants.version, JarAndPathConstants.digest);

	@Override
	public String apply(ICardData from) throws Exception {
		String cardType = typeToDefaultChildMap.get(from.cardType());
		Map<String, Object> data = from.data();
		if (data.containsKey(cardType))
			return cardType;
		if (data.containsKey("nt:unstructured"))
			return "nt:unstructured";
		for (Entry<String, Object> entry : data.entrySet())
			if (entry.getValue() instanceof Map<?, ?>) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) entry.getValue();
				if (CardConstants.group.equals(map.get(CardConstants.slingResourceType)))
					return entry.getKey();
			}
		return null;
	}
}