package org.softwareFm.entityOrganisationData;

import org.softwareFm.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;


public class OrganisationConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("organisation.url", "mainUrl"));
		factory.register(makeMap("name", "text"));
		factory.register(makeMapWithLineEditor("tweets", "list", "Clear", "tweet"));
	}

}
