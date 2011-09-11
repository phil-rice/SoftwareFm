package org.softwareFm.entityJarSummaryData;

import org.softwareFm.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;

public class JarConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("jar", "jarData", "jar", "artifact.jar"));
		// factory.register(makeMapWithView("jar", "project.url", "composite", ArtifactsAnchor.projectKey, "project"));
	}

}
