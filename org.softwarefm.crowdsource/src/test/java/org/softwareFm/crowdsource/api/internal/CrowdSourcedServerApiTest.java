package org.softwareFm.crowdsource.api.internal;

import java.io.File;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.ICrowdSourcedReaderApi;
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
		ICrowdSourcedReaderApi reader1 = api.makeContainer();
		ICrowdSourcedReaderApi reader2 = api.makeContainer();
		IContainer readWriter1 = api.makeContainer();
		IContainer readWriter2 = api.makeContainer();
		
		assertSame(reader1, reader2);
		assertSame(reader1, readWriter1);
		assertSame(reader1, readWriter2);
	}

}
