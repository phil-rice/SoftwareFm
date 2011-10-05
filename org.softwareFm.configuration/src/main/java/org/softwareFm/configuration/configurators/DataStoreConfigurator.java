package org.softwareFm.configuration.configurators;

import org.softwareFm.configuration.ArtifactUrlGenerator;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IGuiDataStoreConfigurator;
import org.softwareFm.display.urlGenerator.UrlGenerator;


public class DataStoreConfigurator implements IGuiDataStoreConfigurator {

	@Override
	public void process(GuiDataStore dataStore) {
		dataStore.//
				urlGenerator("urlGenerator.artifact", new ArtifactUrlGenerator()).//
				urlGenerator("urlGenerator.group", new UrlGenerator("group")).//
				urlGenerator("urlGenerator.user", new UrlGenerator("user")).//
				entity("artifact", "urlGenerator.artifact").//
				dependant("artifact", "group", "artifactId", "urlGenerator.group");
	}

}
