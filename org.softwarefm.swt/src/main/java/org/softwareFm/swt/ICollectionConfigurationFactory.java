/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt;

import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.url.IUrlGeneratorMap;
import org.softwareFm.common.url.UrlGenerator;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ILineItemFunction;
import org.softwareFm.swt.card.IRightClickCategoriser;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.internal.SoftwareFmCardConfigurator;
import org.softwareFm.swt.internal.SoftwareFmCardNameFunction;
import org.softwareFm.swt.internal.SoftwareFmCardValueFunction;
import org.softwareFm.swt.internal.SoftwareFmDefaultChildFunction;
import org.softwareFm.swt.internal.SoftwareFmRightClickCategoriser;

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
			String dataPrefix = prefix + "/" + data + "/";
			final IUrlGeneratorMap urlGeneratorMap = IUrlGeneratorMap.Utils.urlGeneratorMap(//
					CardConstants.groupUrlKey, new UrlGenerator(dataPrefix + "{3}/{2}", SoftwareFmConstants.groupId),// hash, hash, groupId, groundIdWithSlash
					CardConstants.snippetUrlKey, new UrlGenerator(prefix + "/snippet/{3}", CardConstants.snippet),// 0,1: hash, 2,3: snippet
					CardConstants.jarNameUrlKey, new UrlGenerator(prefix + "/jarname/{0}/{1}/{2}", CollectionConstants.jarStem),// 0,1: hash,
					CardConstants.artifactUrlKey, new UrlGenerator(dataPrefix + "{3}/{2}/artifact/{4}", SoftwareFmConstants.groupId, SoftwareFmConstants.artifactId),// 0,1: hash, 2,3: groupId, 4,5: artifactId
					CardConstants.versionCollectionUrlKey, new UrlGenerator(dataPrefix + "{3}/{2}/artifact/{4}/version", SoftwareFmConstants.groupId, SoftwareFmConstants.artifactId, SoftwareFmConstants.version),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
					CardConstants.versionUrlKey, new UrlGenerator(dataPrefix + "{3}/{2}/artifact/{4}/version/{6}", SoftwareFmConstants.groupId, SoftwareFmConstants.artifactId, SoftwareFmConstants.version),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
					CardConstants.digestUrlKey, new UrlGenerator(dataPrefix + "{3}/{2}/artifact/{4}/version/{6}/digest/{8}", SoftwareFmConstants.groupId, SoftwareFmConstants.artifactId, SoftwareFmConstants.version, SoftwareFmConstants.digest),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version, 8,9: digest
					CardConstants.jarUrlKey, SoftwareFmConstants.jarUrlGenerator(prefix),//
					CardConstants.jarUrlRootKey, new UrlGenerator(prefix + "/jars/{0}/{1}", SoftwareFmConstants.digest),// 0,1: hash, 2,3: digest
//					CardConstants.projectUrlKey, ServerConstants.projectGenerator(),//
					CardConstants.userUrlKey, LoginConstants.userGenerator(prefix));
			return urlGeneratorMap;
		}

		public static ICardConfigurator softwareFmConfigurator() {
			return new SoftwareFmCardConfigurator();
		}

	}
}