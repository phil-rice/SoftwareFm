package org.softwareFm.entityProjectData;

import org.softwareFm.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;


public class ProjectConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("project.url", "mainUrl"));
		factory.register(makeMap("project.name", "text"));
		factory.register(makeMapWithLineEditor("mailingLists", "list", "Clear", "nameValue"));
		factory.register(makeMapWithLineEditor("tutorials", "list", "Clear", "nameUrl"));
	}

}
