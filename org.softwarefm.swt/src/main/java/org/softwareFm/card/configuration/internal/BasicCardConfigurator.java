/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.configuration.internal;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.IFollowOnFragment;
import org.softwareFm.card.details.IDetailAdder;
import org.softwareFm.card.details.IDetailFactory;
import org.softwareFm.card.editors.IEditorDetailAdder;
import org.softwareFm.card.modifiers.ICardDataModifier;
import org.softwareFm.card.navigation.ITitleBarForCard;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.swt.card.ILineItemFunction;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;

public class BasicCardConfigurator implements ICardConfigurator {

	@Override
	public CardConfig configure(final Display display, CardConfig config) {
		final IFunction1<String, IResourceGetter> resourceGetterFn = Utils.resourceGetterFn(CardConfig.class, "Card");
		IFunction1<String, Image> imageFn = Utils.imageFn(display, new BasicImageRegisterConfigurator());

		return config.//
				withImageFn(imageFn).//
				withNameFn(ILineItemFunction.Utils.nameFunction(resourceGetterFn, CardConstants.namePattern)).//
				withValueFn(ILineItemFunction.Utils.valueFunction(resourceGetterFn, CardConstants.valuePattern)).//
				withHideFn(ILineItemFunction.Utils.hideFunction(resourceGetterFn, CardConstants.keysHideKey)).//
				withResourceGetterFn(resourceGetterFn).//
				withNavIconFn(ITitleBarForCard.Utils.navIconFn(resourceGetterFn, imageFn)).//
				withDetailsFactory(IDetailFactory.Utils.detailsFactory(//
						IDetailAdder.Utils.collections(),//
						IDetailAdder.Utils.collectionItem(),//
						IDetailAdder.Utils.list(),//
						IDetailAdder.Utils.textView(),//
						IDetailAdder.Utils.editor())).//
				withCardDataModifiers(//
						ICardDataModifier.Utils.collectionsAggregator(CardConstants.slingResourceType), //
						ICardDataModifier.Utils.folderAggregator(CardConstants.jcrPrimaryType, CardConstants.ntUnstructured, CardConstants.slingResourceType), //
						ICardDataModifier.Utils.missingItems(), //
						ICardDataModifier.Utils.sorter(CardConstants.version)).//
				withFollowOn(IFollowOnFragment.Utils.followOnMaps).//

				withTitleSpecFn(TitleSpec.cardToTitleSpecFn(display, imageFn)).//
				withEditorFn(IEditorDetailAdder.Utils.defaultEditorFn());
	}

}