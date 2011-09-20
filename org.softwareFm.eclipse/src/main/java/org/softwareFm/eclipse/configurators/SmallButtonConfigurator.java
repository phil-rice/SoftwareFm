package org.softwareFm.eclipse.configurators;

import org.softwareFm.display.SmallButtonFactory;
import org.softwareFm.display.smallButtons.ISmallButtonConfigurator;
import org.softwareFm.display.smallButtons.SmallButtonStore;
import org.softwareFm.eclipse.jar.JarSimpleButtonFactory;

public class SmallButtonConfigurator implements ISmallButtonConfigurator{

	@Override
	public void process(SmallButtonStore t) throws Exception {
		t.smallButton("smallButton.normal", new SmallButtonFactory()).//
		smallButton("smallButton.jar", new JarSimpleButtonFactory());
	}

}
