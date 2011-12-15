package org.softwareFm.server.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.softwareFm.server.IGitClient;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

public class GitClientMock implements IGitClient {
	public final List<String> urls = Lists.newList();
	private final Map<String, String> map;

	public GitClientMock(String... urlAndRootUrls) {
		this.map = Maps.makeMap((Object[]) urlAndRootUrls);
	}

	@Override
	public Future<String> cloneOrPull(String url, ICallback<String> callback) {
		urls.add(url);
		Assert.assertTrue(map.containsKey(url));
		String result = map.get(url);
		ICallback.Utils.call(callback, result);
		return Futures.doneFuture(result);
	}
}
