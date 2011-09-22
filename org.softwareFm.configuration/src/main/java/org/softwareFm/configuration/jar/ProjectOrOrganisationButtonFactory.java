package org.softwareFm.configuration.jar;

import org.softwareFm.display.displayer.ISmallDisplayer;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.display.smallButtons.ISmallButtonFactory;
import org.softwareFm.display.smallButtons.ImageButtonConfig;
import org.softwareFm.display.smallButtons.SmallButtonDefn;

public class ProjectOrOrganisationButtonFactory implements ISmallButtonFactory {

	private final String dataKey;
	

	public ProjectOrOrganisationButtonFactory(String dataKey) {
		this.dataKey = dataKey;
	}


	@Override
	public ISmallDisplayer create(IButtonParent parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config) {
		return new ProjectOrOrganisationImageButton(parent, config, dataKey);
	}

}
