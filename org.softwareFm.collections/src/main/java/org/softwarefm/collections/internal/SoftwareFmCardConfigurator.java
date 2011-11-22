package org.softwarefm.collections.internal;

import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwarefm.collections.ICollectionConfigurationFactory;

public class SoftwareFmCardConfigurator implements ICardConfigurator {


	@Override
	public CardConfig configure(final Display display, CardConfig config) {
		CardConfig baseConfigured = Utils.basicConfigurator().configure(display, config);
		return baseConfigured.//
				withNameFn(ICollectionConfigurationFactory.Utils.softwareFmNameFunction(baseConfigured.resourceGetterFn, CardConstants.namePattern)).//
				withValueFn(ICollectionConfigurationFactory.Utils.softwareFmValueFunction(baseConfigured.resourceGetterFn, CardConstants.valuePattern)).//
				withDefaultChildFn(ICollectionConfigurationFactory.Utils.softwareFmDefaultChildFunction()).//
				withRightClickCategoriser(ICollectionConfigurationFactory.Utils.softwareFmRightClickCategoriser()).//
				withUrlGeneratorMap(ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap(CardConstants.dataPrefix));//

	}

}
