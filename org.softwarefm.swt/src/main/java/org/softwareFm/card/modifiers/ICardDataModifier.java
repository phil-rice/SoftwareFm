/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.modifiers;

import java.util.Map;

import org.softwareFm.card.modifiers.internal.CardMapSorter;
import org.softwareFm.card.modifiers.internal.CollectionsAggregatorModifier;
import org.softwareFm.card.modifiers.internal.FolderAggregatorModifier;
import org.softwareFm.card.modifiers.internal.HideColectionsModifier;
import org.softwareFm.card.modifiers.internal.KeyValueMissingItemsAdder;
import org.softwareFm.swt.configuration.CardConfig;

/** There are a chain of these modifiers (typically ending with the sorter). Each is responsible for aggregating / enriching / hiding / sorting... data about the card */
public interface ICardDataModifier {

	/** The result can be the rawData is no changes have been made. The rawData should be treated as immutable */
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData);

	public static class Utils {

		/** This should probably be the last item in the chain of card data modifiers. It sorts the lines. The version is the tag of any string that represents a version string: e.g. 2.4.7, so that versions can be sorted correctly. */
		public static ICardDataModifier sorter(String version) {
			return new CardMapSorter(version);
		}

		/** tagName is usually sling:resourceType. This groups children with the same tag value together */
		public static ICardDataModifier collectionsAggregator(String tagName) {
			return new CollectionsAggregatorModifier(tagName);
		}

		/** tagName is usually jcr:primaryType, lookedfor is nt:unstructured, ignored is sling:resourceType. This groups children with the lookedfor tagname together */
		// TODO examine whether need all these parameters, and check how it interacts with the CollectionsAggregatorModifier
		public static ICardDataModifier folderAggregator(String tagName, String lookedFor, String ignoreTag) {
			return new FolderAggregatorModifier(tagName, lookedFor, ignoreTag);
		}

		/** This adds missing items (defined in the xxx.properties file with the key card.missing.string or card.missing.list) */
		public static ICardDataModifier missingItems() {
			return new KeyValueMissingItemsAdder();
		}

		public static ICardDataModifier hideCollections() {
			return new HideColectionsModifier();
		}
	}

}