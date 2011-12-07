/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.internal;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class SoftwareFmCardConfigurator implements ICardConfigurator {

	@Override
	public CardConfig configure(final Display display, CardConfig config) {
		final CardConfig baseConfigured = Utils.basicConfigurator().configure(display, config);
		return baseConfigured.//
				withNameFn(ICollectionConfigurationFactory.Utils.softwareFmNameFunction(baseConfigured.resourceGetterFn, CardConstants.namePattern)).//
				withValueFn(ICollectionConfigurationFactory.Utils.softwareFmValueFunction(baseConfigured.resourceGetterFn, CardConstants.valuePattern)).//
				withTitleFn(ICollectionConfigurationFactory.Utils.softwareFmTitleFunction(baseConfigured.resourceGetterFn)).//
				withDefaultChildFn(ICollectionConfigurationFactory.Utils.softwareFmDefaultChildFunction()).//
				withRightClickCategoriser(ICollectionConfigurationFactory.Utils.softwareFmRightClickCategoriser()).//
				withIconFn(new IFunction1<LineItem, Image>() {
					@SuppressWarnings("unchecked")
					@Override
					public Image apply(LineItem from) throws Exception {
						Image fromKey = getImageFor(from.cardType, from.key);
						if (fromKey != null)
							return fromKey;
						if (from.value instanceof Map<?, ?>) {
							Map<String, Object> map = (Map<String, Object>) from.value;
							String cardType = (String) map.get(CardConstants.slingResourceType);
							Image image = getImageFor(from.cardType, cardType);
							if (image != null)
								return null;
						}
						return baseConfigured.imageFn.apply(ArtifactsAnchor.nothing);
					}

					private Image getImageFor(String cardType,String key) throws Exception {
						String fullKey = Strings.replaceColonWithUnderscore(key) + ".image";
						String imageName = IResourceGetter.Utils.getOrNull(baseConfigured.resourceGetterFn, cardType, fullKey);
						if (imageName != null)
							return baseConfigured.imageFn.apply(imageName);
						else
							return null;
					}
				}).withUrlGeneratorMap(ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap(CardConstants.dataPrefix));//

	}

}