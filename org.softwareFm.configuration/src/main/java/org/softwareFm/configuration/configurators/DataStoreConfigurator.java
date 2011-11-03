package org.softwareFm.configuration.configurators;

import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IGuiDataStoreConfigurator;
import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.jdtBinding.api.JdtConstants;

public class DataStoreConfigurator implements IGuiDataStoreConfigurator {

	@Override
	public void process(GuiDataStore dataStore) {
		String prefix = "/softwareFm/data/";
		dataStore.//
				urlGenerator("urlGenerator.group", new UrlGenerator(prefix + "{3}/{2}", "groupId")).// hash, hash, groupId, groundIdWithSlash
				urlGenerator("urlGenerator.artifact", new UrlGenerator(prefix+"{3}/{2}/artifact/{4}", "groupId", "artifactId")).// 0,1: hash, 2,3: groupId, 4,5: artifactId
				urlGenerator("urlGenerator.version", new UrlGenerator(prefix+"{3}/{2}/artifact/{4}/version/{6}", "groupId", "artifactId", "version")).// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
				urlGenerator("urlGenerator.digest", new UrlGenerator(prefix+"{3}/{2}/artifact/{4}/version/{6}/digest/{8}", "groupId", "artifactId", "version",JdtConstants.hexDigestKey)).// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version, 8,9: digest
//				urlGenerator("urlGenerator", new UrlGenerator(prefix+"{3}/{2}/artifact/{4}/version/{6}", "groupId", "artifactId", "version")).// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
				urlGenerator("urlGenerator.jar", new UrlGenerator("/softwareFm/jars/{0}/{1}/{2}", JdtConstants.hexDigestKey)).// 0,1: hash, 2,3: digest
				urlGenerator("urlGenerator.user", new UrlGenerator("/softwareFm/users/guid/{0}/{1}/{2}", "notSure")).// hash and guid
				entity("jar", "urlGenerator.jar").//
				dependant("jar", "artifact", "urlGenerator.artifact").//
				dependant("jar", "group", "urlGenerator.group");
	}

}
