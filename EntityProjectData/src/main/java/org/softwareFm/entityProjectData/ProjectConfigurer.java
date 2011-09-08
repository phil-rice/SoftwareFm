package org.softwareFm.entityProjectData;

import org.softwareFm.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;

public class ProjectConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("project.url", "mainUrl", "artifact.project"));
		factory.register(makeMap("project.name", "text"));
		factory.register(makeMap("issues", "text"));
		factory.register(makeMapWithLineEditor("mailingLists", "list", ArtifactsAnchor.mailingListKey, "nameValue"));
		factory.register(makeMapWithLineEditor("tutorials", "list", ArtifactsAnchor.tutorialsKey, "nameUrl"));
		factory.register(makeMapWithLineEditor("tweets", "list", ArtifactsAnchor.tweetKey, "tweet"));
	}

}
