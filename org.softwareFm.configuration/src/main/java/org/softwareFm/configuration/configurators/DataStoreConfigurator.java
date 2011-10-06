package org.softwareFm.configuration.configurators;

import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IGuiDataStoreConfigurator;
import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.jdtBinding.api.JdtConstants;

public class DataStoreConfigurator implements IGuiDataStoreConfigurator {

	@Override
	public void process(GuiDataStore dataStore) {
		dataStore.//
				urlGenerator("urlGenerator.group", new UrlGenerator("/softwareFm/groups/{0}/{2}", "groupId")).// hash, groupId
				urlGenerator("urlGenerator.artifact", new UrlGenerator("/softwareFm/artifacts/{0}/{2}/{3}", "groupId", "artifactId")).// hash, groupId, artifactId
				urlGenerator("urlGenerator.version", new UrlGenerator("/softwareFm/versions/{0}/{2}/{3}/{4}", "groupId", "artifactId", "version")).// hash, groupId, artifactId, versionId
				urlGenerator("urlGenerator.jar", new UrlGenerator("/softwareFm/jars/{1}/{2}", JdtConstants.hexDigestKey)).// hash, digest
				urlGenerator("urlGenerator.user", new UrlGenerator("/softwareFm/users/guid/{0}/{2}", "notSure")).// hash and guid
				entity("jar", "urlGenerator.jar").//
				dependant("jar", "group", "urlGenerator.group").//
				dependant("jar", "artifact", "urlGenerator.artifact").//
				dependant("jar", "version", "urlGenerator.version");
	}

}
