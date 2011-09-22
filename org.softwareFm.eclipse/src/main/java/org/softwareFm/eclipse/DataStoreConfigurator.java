package org.softwareFm.eclipse;

import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IGuiDataStoreConfigurator;
import org.softwareFm.display.urlGenerator.UrlGenerator;


public class DataStoreConfigurator implements IGuiDataStoreConfigurator {

	@Override
	public void process(GuiDataStore dataStore) {
		dataStore.//
				urlGenerator("urlGenerator.jar", new JarUrlGenerator()).//
				urlGenerator("urlGenerator.project", new UrlGenerator("project")).//
				urlGenerator("urlGenerator.organisation", new UrlGenerator("organisation")).//
				urlGenerator("urlGenerator.user", new UrlGenerator("user")).//
				entity("jar", "urlGenerator.jar").//
				dependant("jar", "project", "project.url", "urlGenerator.project").//
				dependant("jar", "organisation", "organisation.url", "urlGenerator.organisation");
	}

}
