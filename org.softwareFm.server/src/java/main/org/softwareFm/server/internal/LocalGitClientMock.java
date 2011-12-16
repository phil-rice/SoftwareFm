package org.softwareFm.server.internal;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.ILocalGitClientReader;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class LocalGitClientMock implements ILocalGitClientReader {

	private final Map<String, Object> map;
	public final List<String> urls = Lists.newList();

	public LocalGitClientMock(Object... contents) {
		this.map = Maps.stringObjectMap(contents);
	}

	@Override
	public GetResult localGet(String url) {
		urls.add(url);
		return GetResult.create(map.get(url));
	}

	@Override
	public File getRoot() {
		Assert.fail();
		return null;
	}



}
