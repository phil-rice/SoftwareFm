package org.softwareFm.crowdsource.api.internal;

import java.io.File;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.server.IMailer;

public class CrowdSourcedServerApiTest extends TestCase {
	private final File root = new File("root");
	private final ServerConfig serverConfig = ServerConfig.serverConfigForTests(root, IMailer.Utils.noMailer());

	public void testConstructor() {
		ICrowdSourcedApi api1 = ICrowdSourcedApi.Utils.forServer(serverConfig);
		assertTrue(api1 instanceof CrowdSourcedServerApi);
		ICrowdSourcedApi api2 = ICrowdSourcedApi.Utils.forServer(serverConfig);
		assertNotSame(api1, api2);
	}

	public void testGettersReturnsSameObjectEachTime() {
		ICrowdSourcedApi api = ICrowdSourcedApi.Utils.forServer(serverConfig);
		assertTrue(api instanceof CrowdSourcedServerApi);
		IContainer container1 = api.makeContainer();
		IContainer container2 = api.makeContainer();
		
		assertSame(container1, container2);
	}

}
