package org.softwareFm.server.internal;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.ILocalGitClientReader;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class LocalGitClientMock implements ILocalGitClientReader {

	private final Map<String, Object> map;
	public final List<IFileDescription> urls = Lists.newList();

	public LocalGitClientMock(Object... contents) {
		this.map = Maps.stringObjectMap(contents);
	}

	@Override
	public GetResult localGet(IFileDescription fileDescription) {
		urls.add(fileDescription);
		return GetResult.create(map.get(fileDescription));
	}

	@Override
	public File getRoot() {
		Assert.fail();
		return null;
	}

	@Override
	public GetResult getFile(IFileDescription fileDescription) {
		throw new UnsupportedOperationException();
	}

}
