package org.arc4eclipse.entityProjectData;

import org.arc4eclipse.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;

public class ProjectConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("project.url", "mainUrl"));
		factory.register(makeMap("project.name", "text"));
		factory.register(makeMapWithLineEditor("mailingLists", "list", "Clear", "nameValue"));
		factory.register(makeMapWithLineEditor("tutorials", "list", "Clear", "nameUrl"));
	}

}
