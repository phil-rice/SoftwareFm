package org.softwareFm.server.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.server.GetResult;
import org.softwareFm.server.ILocalGitReader;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class LocalGitReaderMock implements ILocalGitReader {

	private final Map<String, Object> map;
	public final List<String> urls = Lists.newList();

	public LocalGitReaderMock(Object... contents) {
		this.map = Maps.stringObjectMap(contents);
	}

	@Override
	public GetResult get(String url) {
		urls.add(url);
		return GetResult.create(map.get(url));
	}

	@Override
	public String cloneOrPull(String url) {
		throw new UnsupportedOperationException();
	}

}
