package org.softwareFm.configuration.configurators;

import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IGuiDataStoreConfigurator;
import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.jdtBinding.api.JdtConstants;

public class DataStoreConfigurator implements IGuiDataStoreConfigurator {

	@Override
	public void process(GuiDataStore dataStore) {
		dataStore.//
				urlGenerator("urlGenerator.group", new UrlGenerator("/softwareFm/content/{3}", "groupId")).// hash, hash, groupId, groundIdWithSlash
				urlGenerator("urlGenerator.artifact", new UrlGenerator("/softwareFm/content/{3}/artifact/{5}", "groupId", "artifactId")).// 0,1: hash, 2,3: groupId, 4,5: artifactId
				urlGenerator("urlGenerator.version", new UrlGenerator("/softwareFm/content/{3}/artifact/{5}/version/{6}", "groupId", "artifactId", "version")).// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
				urlGenerator("urlGenerator.jar", new UrlGenerator("/softwareFm/jars/{0}/{1}/{2}", JdtConstants.hexDigestKey)).// 0,1: hash, 2,3: digest
				urlGenerator("urlGenerator.user", new UrlGenerator("/softwareFm/users/guid/{0}/{1}/{2}", "notSure")).// hash and guid
				entity("jar", "urlGenerator.jar").//
				dependant("jar", "artifact", "urlGenerator.artifact").//
				dependant("jar", "group", "urlGenerator.group");
	}

}
