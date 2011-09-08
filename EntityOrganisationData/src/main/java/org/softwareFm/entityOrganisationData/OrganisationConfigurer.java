package org.softwareFm.entityOrganisationData;

import org.softwareFm.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;

public class OrganisationConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("organisation.url", "mainUrl", "artifact.organisation"));
		factory.register(makeMap("name", "text"));
		factory.register(makeMapWithLineEditor("tweets", "list", ArtifactsAnchor.tweetKey, "tweet"));
	}

}
