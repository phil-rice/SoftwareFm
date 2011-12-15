package org.softwareFm.server.internal;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.repositoryFacard.IRepositoryFacardReader;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.ILocalGitReader;
import org.softwareFm.utilities.collections.Lists;

public class GitRepositoryFacardTest extends GitTest {

	private final LocalGitReaderMock mockWith = new LocalGitReaderMock("url1", v11);

	public void testDelegatesToLocalGitOnServerThread() throws Exception {
		final Thread mainThread = Thread.currentThread();
		IRepositoryFacardReader repositoryFacard = makeRepostory(mockWith);
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
		LocalGitReaderMultiMock mock = new LocalGitReaderMultiMock("url1", "urlRoot", v11);
		IRepositoryFacardReader repositoryFacard = makeRepostory(mock);
		Future<?> future = repositoryFacard.get("url1", new IRepositoryFacardCallback() {
			@Override
			public void process(IResponse response, Map<String, Object> data) throws Exception {
				assertEquals(v11, data);
			}
		});
		assertEquals(GetResult.create(v11), future.get(1000, TimeUnit.MILLISECONDS));
		assertEquals(3, mock.index.get());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

	}

	private IRepositoryFacardReader makeRepostory(ILocalGitReader localGitReader) {
		return new GitRepositoryFacard(getServiceExecutor(), localGitReader);
	}
}
