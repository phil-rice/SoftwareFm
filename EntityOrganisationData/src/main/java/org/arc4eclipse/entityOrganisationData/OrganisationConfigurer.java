package org.arc4eclipse.entityOrganisationData;

import org.arc4eclipse.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;

public class OrganisationConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("organisation.url", "mainUrl"));
		factory.register(makeMap("name", "text"));
		factory.register(makeMap("tweets", "tweets"));
	}

}
