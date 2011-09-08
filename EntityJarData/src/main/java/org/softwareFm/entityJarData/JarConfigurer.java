package org.softwareFm.entityJarData;

import org.softwareFm.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;

public class JarConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("jarData", "jar", "artifact.jar"));
		factory.register(makeMap("javadoc", "javadoc", "artifact.javadoc"));
		factory.register(makeMap("source", "src", "artifact.source"));
		factory.register(makeMap("project.url", "artifact.project"));
		factory.register(makeMap("organisation.url", "artifact.organisation"));
	}

}
