package org.softwarefm.collections;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ILineItemFunction;
import org.softwareFm.card.card.IPopupMenuContributor;
import org.softwareFm.card.card.IRightClickCategoriser;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.display.data.IUrlGeneratorMap;
import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.collections.internal.CardPopupMenuContributor;
import org.softwarefm.collections.internal.SoftwareFmCardConfigurator;
import org.softwarefm.collections.internal.SoftwareFmCardNameFunction;
import org.softwarefm.collections.internal.SoftwareFmCardValueFunction;
import org.softwarefm.collections.internal.SoftwareFmDefaultChildFunction;
import org.softwarefm.collections.internal.SoftwareFmRightClickCategoriser;

public interface ICollectionConfigurationFactory {
	public static class Utils {

		public static ILineItemFunction<String> softwareFmNameFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String namePattern) {
			return new SoftwareFmCardNameFunction(ILineItemFunction.Utils.nameFunction(resourceGetterFn, namePattern));
		}

		public static ILineItemFunction<String> softwareFmValueFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String valuePattern) {
			return new SoftwareFmCardValueFunction(resourceGetterFn, valuePattern);
		}

		public static IFunction1<ICard, String> softwareFmDefaultChildFunction() {
			return new SoftwareFmDefaultChildFunction();
		}

		public static IRightClickCategoriser softwareFmRightClickCategoriser() {
			return new SoftwareFmRightClickCategoriser();
		}

		public static IPopupMenuContributor<ICard> softwareFmPopupMenuContributor() {
			return new CardPopupMenuContributor();
		}

		public static IFunction1<ICard, String> softwareFmTitleFunction(IFunction1<String, IResourceGetter> resourceGetterFn) {
			return new SoftwareFmCardTitleFunction();
		}

		public static IUrlGeneratorMap makeSoftwareFmUrlGeneratorMap(String prefix) {
			final IUrlGeneratorMap urlGeneratorMap = IUrlGeneratorMap.Utils.urlGeneratorMap(//
					CardConstants.urlGroupKey, new UrlGenerator(prefix + "{3}/{2}", "groupId"),// hash, hash, groupId, groundIdWithSlash
					CardConstants.artifactUrlKey, new UrlGenerator(prefix + "{3}/{2}/artifact/{4}", "groupId", "artifactId"),// 0,1: hash, 2,3: groupId, 4,5: artifactId
					CardConstants.versionUrlKey, new UrlGenerator(prefix + "{3}/{2}/artifact/{4}/version/{6}", "groupId", "artifactId", "version"),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
					CardConstants.digestUrlKey, new UrlGenerator(prefix + "{3}/{2}/artifact/{4}/version/{6}/digest/{8}", "groupId", "artifactId", "version", CardConstants.digest),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version, 8,9: digest
					CardConstants.jarUrlKey, new UrlGenerator("/softwareFm/jars/{0}/{1}/{2}", CardConstants.digest),// 0,1: hash, 2,3: digest
					CardConstants.userUrlKey, new UrlGenerator("/softwareFm/users/guid/{0}/{1}/{2}", "notSure"));// hash and guid
			return urlGeneratorMap;
		}

		public static ICardConfigurator softwareFmConfigurator() {
			return new SoftwareFmCardConfigurator();
		}

	}
}
