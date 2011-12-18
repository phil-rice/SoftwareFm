/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ILineItemFunction;
import org.softwareFm.card.card.IRightClickCategoriser;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.internal.SoftwareFmCardConfigurator;
import org.softwareFm.collections.internal.SoftwareFmCardNameFunction;
import org.softwareFm.collections.internal.SoftwareFmCardValueFunction;
import org.softwareFm.collections.internal.SoftwareFmDefaultChildFunction;
import org.softwareFm.collections.internal.SoftwareFmRightClickCategoriser;
import org.softwareFm.display.data.IUrlGeneratorMap;
import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

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


		public static IFunction1<ICard, String> softwareFmTitleFunction(IFunction1<String, IResourceGetter> resourceGetterFn) {
			return new SoftwareFmCardTitleFunction();
		}

		public static IUrlGeneratorMap makeSoftwareFmUrlGeneratorMap(String prefix, String data) {
			String dataPrefix =prefix+"/"+ data+"/";
			final IUrlGeneratorMap urlGeneratorMap = IUrlGeneratorMap.Utils.urlGeneratorMap(//
					CardConstants.groupUrlKey, new UrlGenerator(dataPrefix + "{3}/{2}", "groupId"),// hash, hash, groupId, groundIdWithSlash
					CardConstants.snippetUrlKey, new UrlGenerator(dataPrefix + "{3}", "snippet"),// 0,1: hash, 2,3: snippet
					CardConstants.artifactUrlKey, new UrlGenerator(dataPrefix + "{3}/{2}/artifact/{4}", "groupId", "artifactId"),// 0,1: hash, 2,3: groupId, 4,5: artifactId
					CardConstants.versionUrlKey, new UrlGenerator(dataPrefix + "{3}/{2}/artifact/{4}/version/{6}", "groupId", "artifactId", "version"),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
					CardConstants.digestUrlKey, new UrlGenerator(dataPrefix + "{3}/{2}/artifact/{4}/version/{6}/digest/{8}", "groupId", "artifactId", "version", CardConstants.digest),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version, 8,9: digest
					CardConstants.jarUrlKey, new UrlGenerator(prefix+"/jars/{0}/{1}/{2}", CardConstants.digest),// 0,1: hash, 2,3: digest
					CardConstants.userUrlKey, new UrlGenerator(prefix+"/users/guid/{0}/{1}/{2}", "notSure"));// hash and guid
			return urlGeneratorMap;
		}

		public static ICardConfigurator softwareFmConfigurator() {
			return new SoftwareFmCardConfigurator();
		}

	}
}