package org.softwareFm.gitwriter;

import java.io.File;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitWriter;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.url.Urls;

public class HttpGitWriterTest extends AbstractProcessorDatabaseIntegrationTests {

	private IGitWriter gitWriter;
	private File remoteAb;
	private File remoteAbDotGit;
	private File remoteAbcData;
	private File remoteAbdData;

	public void testInit() {
		gitWriter.init("a/b");
		assertTrue(remoteAbDotGit.exists());
	}

	public void testPut() {
		gitWriter.init("a/b");
		gitWriter.put(IFileDescription.Utils.plain("a/b/c"), v11);
		assertEquals(v11, Json.parseFile(remoteAbcData));
	}
	
	public void testInitPutAllowsGet(){
		gitWriter.init("a/b");
		gitWriter.put(IFileDescription.Utils.plain("a/b/c"), v11);
		
		localOperations.init("a/b");
		localOperations.setConfigForRemotePull("a/b", remoteRoot.getAbsolutePath());
		localOperations.pull("a/b");
		assertEquals(v11, localOperations.getFile(IFileDescription.Utils.plain("a/b/c")));
	}

	public void testDelete() {
		gitWriter.init("a/b");
		gitWriter.put(IFileDescription.Utils.plain("a/b/c"), v11);
		gitWriter.put(IFileDescription.Utils.plain("a/b/d"), v12);
		
		gitWriter.delete(IFileDescription.Utils.plain("a/b/c"));
		
		assertFalse(remoteAbcData.exists());
		assertEquals(v12, Json.parseFile(remoteAbdData));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gitWriter = new HttpGitWriter(getHttpClient());
		remoteAb = new File(remoteRoot, "a/b");
		remoteAbcData = new File(remoteRoot, Urls.compose("a/b/c", CommonConstants.dataFileName));
		remoteAbdData = new File(remoteRoot, Urls.compose("a/b/d", CommonConstants.dataFileName));
		remoteAbDotGit = new File(remoteRoot, Urls.compose("a/b", CommonConstants.DOT_GIT));
	}

}
