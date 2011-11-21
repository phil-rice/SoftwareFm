package org.softwareFm.card.softwareFm.internal;

import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.card.ILineItemFunction;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;

public class SoftwareFmCardConfigurator implements ICardConfigurator {

	@Override
	public CardConfig configure(final Display display, CardConfig config) {
		CardConfig baseConfigured = Utils.basicConfigurator().configure(display, config);
		return baseConfigured.//
				withNameFn(ILineItemFunction.Utils.softwareFmNameFunction(baseConfigured.resourceGetterFn, CardConstants.namePattern)).//
				withDefaultChildFn(new SoftwareFmDefaultChildFunction()).//
				withRightClickCategoriser(new SoftwareFmRightClickCategoriser()).//
				withUrlGeneratorMap(ICardConfigurator.Utils.makeSoftwareFmUrlGeneratorMap(CardConstants.dataPrefix));//

	}


}
