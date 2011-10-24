package org.softwareFm.display.browser;

import org.softwareFm.display.composites.CompositeConfig;

public interface IBrowserConfigurator {

	void configure(CompositeConfig config, IBrowserCompositeBuilder builder);
}
