package org.softwareFm.collections.explorer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

public class UsageStrategyMock implements IUsageStrategy {

	public final List<String> softwareFmIds = Lists.newList();
	public final List<String> groupIds = Lists.newList();
	public final List<String> artifactIds = Lists.newList();
	public final List<String> cryptos = Lists.newList();
	private final Map<String, Object> projectData;
	public final AtomicInteger usingCount = new AtomicInteger();
	public final AtomicInteger myProjectDataCount = new AtomicInteger();

	public UsageStrategyMock(Object... namesAndValues) {
		projectData = Maps.stringObjectMap(namesAndValues);
	}

	@Override
	public Future<?> using(String softwareFmId, String groupId, String artifactId, IResponseCallback callback) {
		softwareFmIds.add(softwareFmId);
		groupIds.add(groupId);
		artifactIds.add(artifactId);
		usingCount.incrementAndGet();
		callback.process(IResponse.Utils.okText("", ""));
		return Futures.doneFuture(null);
	}

	@Override
	public Map<String, Object> myProjectData(String softwareFmId, String crypto) {
		softwareFmIds.add(softwareFmId);
		cryptos.add(crypto);
		myProjectDataCount.incrementAndGet();
		return projectData;
	}

}
