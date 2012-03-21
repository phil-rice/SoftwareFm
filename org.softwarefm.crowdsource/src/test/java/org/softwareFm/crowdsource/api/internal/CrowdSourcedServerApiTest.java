package org.softwareFm.crowdsource.api.internal;

import java.io.File;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.api.ICrowdSourceReadWriteApi;
import org.softwareFm.crowdsource.api.ICrowdSourceReaderApi;
import org.softwareFm.crowdsource.api.ICrowdSourcesApi;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.server.IMailer;

public class CrowdSourcedServerApiTest extends TestCase {
	private final File root = new File("root");
	private final ServerConfig serverConfig = ServerConfig.serverConfigForTests(root, IMailer.Utils.noMailer());

	public void testConstructor() {
		ICrowdSourcesApi api1 = ICrowdSourcesApi.Utils.forServer(serverConfig);
		assertTrue(api1 instanceof CrowdSourcedServerApi);
		ICrowdSourcesApi api2 = ICrowdSourcesApi.Utils.forServer(serverConfig);
		assertNotSame(api1, api2);
	}

	public void testGettersReturnsSameObjectEachTime() {
		ICrowdSourcesApi api = ICrowdSourcesApi.Utils.forServer(serverConfig);
		assertTrue(api instanceof CrowdSourcedServerApi);
		ICrowdSourceReaderApi reader1 = api.makeReader();
		ICrowdSourceReaderApi reader2 = api.makeReader();
		ICrowdSourceReadWriteApi readWriter1 = api.makeReadWriter();
		ICrowdSourceReadWriteApi readWriter2 = api.makeReadWriter();
		
		assertSame(reader1, reader2);
		assertSame(reader1, readWriter1);
		assertSame(reader1, readWriter2);
	}

}
