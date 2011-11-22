package org.softwareFm.card.configuration.internal;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ILineItemFunction;
import org.softwareFm.card.card.IPopupMenuContributor;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.IFollowOnFragment;
import org.softwareFm.card.details.IDetailAdder;
import org.softwareFm.card.details.IDetailFactory;
import org.softwareFm.card.editors.IEditorDetailAdder;
import org.softwareFm.card.modifiers.ICardDataModifier;
import org.softwareFm.card.navigation.ITitleBarForCard;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

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
						IDetailAdder.Utils.listDetail(),//
						IDetailAdder.Utils.editorDetail())).//
				withKeyValueModifiers(//
						ICardDataModifier.Utils.collectionsAggregator(CardConstants.slingResourceType), //
						ICardDataModifier.Utils.folderAggregator(CardConstants.jcrPrimaryType, CardConstants.ntUnstructured, CardConstants.slingResourceType), //
						ICardDataModifier.Utils.missingItems(), //
						ICardDataModifier.Utils.sorter(CardConstants.version)).//
				withFollowOn(IFollowOnFragment.Utils.followOnMaps).//

				withTitleSpecFn(TitleSpec.cardToTitleSpecFn(display, imageFn)).//
				withPopupMenuContributor(IPopupMenuContributor.Utils.<ICard> noContributor()).//
				withEditorFn(IEditorDetailAdder.Utils.defaultEditorFn());
	}

}
