package org.softwareFm.entityOrganisationData;

import org.softwareFm.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;

public class OrganisationConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("organisation", "organisation.url", "mainUrl", "artifact.organisation"));
		factory.register(makeMap("organisation", "name", "text"));
		factory.register(makeMapWithLineEditor("organisation", "tweets", "list", ArtifactsAnchor.twitterKey, "tweet"));
	}

}
