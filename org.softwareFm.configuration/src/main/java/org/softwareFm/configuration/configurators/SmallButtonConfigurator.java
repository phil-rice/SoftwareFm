package org.softwareFm.configuration.configurators;

import org.softwareFm.configuration.jar.JarButtonFactory;
import org.softwareFm.configuration.jar.ProjectOrOrganisationButtonFactory;
import org.softwareFm.display.smallButtons.ISmallButtonConfigurator;
import org.softwareFm.display.smallButtons.SmallButtonFactory;
import org.softwareFm.display.smallButtons.SmallButtonStore;

public class SmallButtonConfigurator implements ISmallButtonConfigurator {

	@Override
	public void process(SmallButtonStore t) throws Exception {
		t.smallButton("smallButton.normal", new SmallButtonFactory()).//
				smallButton("smallButton.jar", new JarButtonFactory()).//
				smallButton("smallButton.project", new ProjectOrOrganisationButtonFactory(LargeButtonFactory.dataJarProjectUrl)).//
				smallButton("smallButton.organisation", new ProjectOrOrganisationButtonFactory(LargeButtonFactory.dataJarOrganisationUrl));
	}
}
