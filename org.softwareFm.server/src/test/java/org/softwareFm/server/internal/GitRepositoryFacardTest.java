package org.softwareFm.server.internal;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.repositoryFacard.IRepositoryFacardReader;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.server.ILocalGitClientReader;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.future.Futures;

public class GitRepositoryFacardTest extends GitTest {

	private final LocalGitClientMock mockWith = new LocalGitClientMock("url1", v11);
	private IHttpClient httpClient;

	public void testGetDelegatesToLocalGitOnServerThread() throws Exception {
		final Thread mainThread = Thread.currentThread();
		IRepositoryFacardReader repositoryFacard = makeRepostory(mockWith, gitFacard, "notUsed", "notUsed");
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

	public void testClonesRepositoryToPlaceSpecifiedBySoftwareFmServerIfNotFound() throws Exception {
		fail("used to mock the clone operation...probably doesnt any more ");
		LocalGitClientMultiMock mock = new LocalGitClientMultiMock("url1", v11);
		IGitFacard gitFacard = new GitFacardMock();
		IRepositoryFacardReader repositoryFacard = makeRepostory(mock, gitFacard, "remoteGitPrefix", "foundRootUrl");
		Future<?> future = repositoryFacard.get("url1", new IRepositoryFacardCallback() {
			@Override
			public void process(IResponse response, Map<String, Object> data) throws Exception {
				assertEquals(v11, data);
			}
		});
		assertEquals(GetResult.create(v11), future.get(10000000, TimeUnit.MILLISECONDS));
		assertEquals(2, mock.index.get());
	}

	private IRepositoryFacardReader makeRepostory(ILocalGitClientReader localGitClientReader, IGitFacard gitFacard, String remoteGitPrefix, final String foundRootUrl) {
		return new GitRepositoryFacard(getHttpClient(), getServiceExecutor(), gitFacard, localGitClientReader, remoteGitPrefix) {
			@Override
			public Future<?> findRepositoryBase(String url, ICallback<String> callback) {
				ICallback.Utils.call(callback, foundRootUrl);
				return Futures.doneFuture(null);
			}
		};
	}
}
