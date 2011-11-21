package org.softwareFm.card.card;

import java.util.Map;

import org.softwareFm.card.modifiers.internal.CardMapSorter;
import org.softwareFm.card.modifiers.internal.CollectionsAggregatorModifier;
import org.softwareFm.card.modifiers.internal.FolderAggregatorModifier;
import org.softwareFm.card.modifiers.internal.KeyValueMissingItemsAdder;

/** There are a chain of these modifiers (typically ending with the sorter). Each is responsible for aggregating / enriching / hiding / sorting... data about the card */
public interface ICardDataModifier {

	/** The result can be the rawData is no changes have been made. The rawData should be treated as immutable */
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData);

	public static class Utils {
		
		/** This should probably be the last item in the chain of card data modifiers. It sorts the lines. The version is the tag of any string that represents a version string: e.g. 2.4.7, so that versions can be sorted correctly. */
		public static ICardDataModifier sorter(String version) {
			return new CardMapSorter(version);
		}
		
		/** tagName is usually sling:resourceType. This groups children with the same tag value together*/
		public static ICardDataModifier collectionsAggregator(String tagName) {
			return new CollectionsAggregatorModifier(tagName);
		}
		
		/** tagName is usually jcr:primaryType, lookedfor is nt:unstructured, ignored is sling:resourceType. This groups children with the lookedfor tagname together*/
		//TODO examine whether need all these parameters, and check how it interacts with the CollectionsAggregatorModifier
		public static ICardDataModifier folderAggregator(String tagName, String lookedFor, String ignoreTag){
			return new FolderAggregatorModifier(tagName, lookedFor, ignoreTag);
		}
		
		/** This adds missing items (defined in the xxx.properties file with the key card.missing.string or card.missing.list) */
		public static ICardDataModifier missingItems(){
			return new KeyValueMissingItemsAdder();
		}
	}

}
