package org.softwareFm.eclipse.configurators;

import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IGuiDataStoreConfigurator;
import org.softwareFm.display.urlGenerator.JarUrlGenerator;
import org.softwareFm.display.urlGenerator.UrlGenerator;


public class DataStoreConfigurator implements IGuiDataStoreConfigurator {

	@Override
	public void process(GuiDataStore dataStore) {
		dataStore.//
				urlGenerator("urlGenerator.jar", new JarUrlGenerator()).//
				urlGenerator("urlGenerator.project", new UrlGenerator("project")).//
				urlGenerator("urlGenerator.organisation", new UrlGenerator("organisation")).//
				entity("jar", "urlGenerator.jar").//
				dependant("jar", "project", "jar.projectUrl", "urlGenerator.project").//
				dependant("jar", "organisation", "jar.organisationUrl", "urlGenerator.organisation");
	}

}
