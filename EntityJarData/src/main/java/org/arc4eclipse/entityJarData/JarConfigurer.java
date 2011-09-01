package org.arc4eclipse.entityJarData;

import org.arc4eclipse.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;

public class JarConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("jarData", "jar", "Jar"));
		factory.register(makeMap("javadoc", "javadoc", "Javadoc"));
		factory.register(makeMap("source", "src", "Source"));
		factory.register(makeMap("project.url", "url"));
		factory.register(makeMap("organisation.url", "url"));
	}

}
