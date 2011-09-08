package org.softwareFm.entityProjectData;

import org.softwareFm.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;

public class ProjectConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("project.url", "mainUrl", "artifact.project"));
		factory.register(makeMap("project.name", "text"));
		factory.register(makeMap("issues", "text"));
		factory.register(makeMapWithLineEditor("mailingLists", "list", "Clear", "nameValue"));
		factory.register(makeMapWithLineEditor("tutorials", "list", "Clear", "nameUrl"));
		factory.register(makeMapWithLineEditor("tweets", "list", "Clear", "tweet"));
	}

}
