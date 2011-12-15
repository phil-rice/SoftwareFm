package org.softwareFm.server.internal;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.repositoryFacard.IRepositoryFacardReader;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IGitClient;
import org.softwareFm.server.ILocalGitClientReader;
import org.softwareFm.utilities.collections.Lists;

public class GitRepositoryFacardTest extends GitTest {

	private final LocalGitClientMock mockWith = new LocalGitClientMock("url1", v11);

	public void testDelegatesToLocalGitOnServerThread() throws Exception {
		final Thread mainThread = Thread.currentThread();
		IRepositoryFacardReader repositoryFacard = makeRepostory(mockWith, IGitClient.Utils.noClient());
		Future<?> future = repositoryFacard.get("url1", new IRepositoryFacardCallback() {
			@Override
			public void process(IResponse response, Map<String, Object> data) throws Exception {
				assertNotSame(mainThread, Thread.currentThread());
				assertEquals(v11, data);
			}
		});
		assertEquals(GetResult.create(v11), future.get(1000, TimeUnit.MILLISECONDS));
		assertEquals("url1", Lists.getOnly(mockWith.urls));
	}

	public void testTellsLocalToCloneOrPullIfNotFoundLocally() throws Exception {
		LocalGitClientMultiMock mock = new LocalGitClientMultiMock("url1", v11);
		IGitClient gitClient = new GitClientMock("url1", "urlRoot");
		
		IRepositoryFacardReader repositoryFacard = makeRepostory(mock, gitClient);
		Future<?> future = repositoryFacard.get("url1", new IRepositoryFacardCallback() {
			@Override
			public void process(IResponse response, Map<String, Object> data) throws Exception {
				assertEquals(v11, data);
			}
		});
		assertEquals(GetResult.create(v11), future.get(1000, TimeUnit.MILLISECONDS));
		assertEquals(2, mock.index.get());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

	}

	private IRepositoryFacardReader makeRepostory(ILocalGitClientReader localGitClientReader, IGitClient gitClient) {
		return new GitRepositoryFacard(getServiceExecutor(), gitClient, localGitClientReader);
	}
}
