package org.softwareFm.entityJarSummaryData;

import org.softwareFm.core.plugin.AbstractDisplayContainerFactoryConfigurer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;

public class JarSummaryConfigurer extends AbstractDisplayContainerFactoryConfigurer {

	@Override
	public void configure(IDisplayContainerFactory factory) {
		factory.register(makeMap("jarData", "jarSummary", "Jar"));
		factory.register(makeMap("javadoc", "javadoc", "javadoc"));
		factory.register(makeMap("source", "src", "source"));
	}

}
