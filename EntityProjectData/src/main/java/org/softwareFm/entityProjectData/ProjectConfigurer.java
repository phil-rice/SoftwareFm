package org.softwareFm.entityProjectData;

import org.softwareFm.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;

public class ProjectConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("project", "project.url", "mainUrl", ArtifactsAnchor.projectKey));
		factory.register(makeMap("project", "project.name", "text", ArtifactsAnchor.projectKey));
		factory.register(makeMap("project", "issues", "text", ArtifactsAnchor.issuesKey));
		factory.register(makeMapWithLineEditor("project", "mailingLists", "list", ArtifactsAnchor.mailingListKey, "nameValue"));
		factory.register(makeMapWithLineEditor("project", "tutorials", "list", ArtifactsAnchor.tutorialsKey, "nameUrl"));
		factory.register(makeMapWithLineEditor("project", "tweets", "list", ArtifactsAnchor.twitterKey, "tweet"));
	}

}
