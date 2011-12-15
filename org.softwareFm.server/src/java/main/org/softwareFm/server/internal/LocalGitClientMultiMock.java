package org.softwareFm.server.internal;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.softwareFm.server.GetResult;
import org.softwareFm.server.ILocalGitClientReader;

public class LocalGitClientMultiMock implements ILocalGitClientReader {

	private final String url;
	private final Map<String, Object> result;
	public final AtomicInteger index = new AtomicInteger();

	public LocalGitClientMultiMock(String url, Map<String, Object> map) {
		this.url = url;
		this.result = map;
	}

	@Override
	public GetResult get(String url) {
		switch (index.getAndIncrement()) {
		case 0:
			Assert.assertEquals(this.url, url);
			return GetResult.create(null);
		case 1:
			return GetResult.create(result);
		default:
			Assert.fail(index.toString());
			return null;
		}
	}


}
