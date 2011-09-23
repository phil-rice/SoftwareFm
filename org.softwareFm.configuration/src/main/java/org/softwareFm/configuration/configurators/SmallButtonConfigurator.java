package org.softwareFm.configuration.configurators;

import org.softwareFm.configuration.jar.JarButtonFactory;
import org.softwareFm.display.smallButtons.DataButtonFactory;
import org.softwareFm.display.smallButtons.ISmallButtonConfigurator;
import org.softwareFm.display.smallButtons.SmallButtonFactory;
import org.softwareFm.display.smallButtons.SmallButtonStore;

public class SmallButtonConfigurator implements ISmallButtonConfigurator {

	@Override
	public void process(SmallButtonStore t) throws Exception {
		t.smallButton("smallButton.normal", new SmallButtonFactory()).//
				smallButton("smallButton.jar", new JarButtonFactory()).//
				smallButton("smallButton.data", new DataButtonFactory());
	}
}
