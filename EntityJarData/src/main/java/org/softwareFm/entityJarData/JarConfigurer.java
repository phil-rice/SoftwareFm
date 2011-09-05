package org.softwareFm.entityJarData;

import org.softwareFm.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;

public class JarConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("jarData", "jar", "Jar"));
		factory.register(makeMap("javadoc", "javadoc", "javadoc"));
		factory.register(makeMap("source", "src", "source"));
		factory.register(makeMap("project.url", "url"));
		factory.register(makeMap("organisation.url", "url"));
	}

}
