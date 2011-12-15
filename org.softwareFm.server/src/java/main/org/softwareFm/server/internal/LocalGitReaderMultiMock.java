package org.softwareFm.server.internal;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.softwareFm.server.GetResult;
import org.softwareFm.server.ILocalGitReader;

public class LocalGitReaderMultiMock implements ILocalGitReader {

	private final String firstUrl;
	private final String secondUrl;
	private final Map<String, Object> result;
	public final AtomicInteger index = new AtomicInteger();

	public LocalGitReaderMultiMock(String firstUrl, String secondUrl, Map<String, Object> map) {
		this.firstUrl = firstUrl;
		this.secondUrl = secondUrl;
		this.result = map;
	}

	@Override
	public GetResult get(String url) {
		switch (index.getAndIncrement()) {
		case 0:
			return GetResult.create(null);
		case 2:
			return GetResult.create(result);
		default:
			Assert.fail(index.toString());
			return null;
		}
	}

	@Override
	public String cloneOrPull(String url) {
		Assert.assertEquals(1, index.getAndIncrement());
		return secondUrl;
	}

}
