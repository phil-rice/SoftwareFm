/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.modifiers.internal;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.modifiers.ICardDataModifier;

public class CardMapSorter implements ICardDataModifier {

	private final String version;

	public CardMapSorter(String version) {
		this.version = version;
	}

	@Override
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData) {
		String lastSegment = Strings.lastSegment(url, "/");
		if (lastSegment.equals(version))
			return Maps.sortByKey(rawData, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return Strings.compareVersionNumbers(o1, o2);
				}
			});
		String cardType = (String) rawData.get(CardConstants.slingResourceType);
		List<String> order = Strings.splitIgnoreBlanks(IResourceGetter.Utils.get(cardConfig.resourceGetterFn, cardType, CardConstants.cardOrderKey), ",");
		Comparator<String> orderedComparator = Lists.orderedComparator(order);
		Map<String, Object> result = Maps.sortByKey(rawData, orderedComparator);
		return result;
	}

}