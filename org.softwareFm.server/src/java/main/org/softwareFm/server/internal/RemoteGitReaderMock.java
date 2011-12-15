package org.softwareFm.server.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.softwareFm.server.IRemoteGitReader;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

public class RemoteGitReaderMock implements IRemoteGitReader {

	public final List<String> urls = Lists.newList();
	private final Map<String, String> map;

	public RemoteGitReaderMock(String... urlAndReturnedUrl) {
		this.map = Maps.<String, String> makeMap((Object[]) urlAndReturnedUrl);
	}

	@Override
	public Future<String> cloneOrPull(String url, ICallback<String> callback) {
		urls.add(url);
		Assert.assertTrue(map.containsKey(url));
		String t = map.get(url);
		ICallback.Utils.call(callback, t);
		return Futures.doneFuture(t);
	}

}
