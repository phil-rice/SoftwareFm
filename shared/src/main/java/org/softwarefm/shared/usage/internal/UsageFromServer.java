package org.softwarefm.shared.usage.internal;

import java.util.List;

import org.apache.http.HttpStatus;
import org.softwarefm.server.configurator.ConfiguratorConstants;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.IUsageFromServer;
import org.softwarefm.shared.usage.IUsageFromServerCallback;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;

public class UsageFromServer implements IUsageFromServer {

	private final IHttpClient client;
	private final IUsagePersistance persistance;

	public UsageFromServer(String host, int port, IUsagePersistance persistance) {
		this.persistance = persistance;
		client = IHttpClient.Utils.builder().host(host, port);
	}

	/* (non-Javadoc)
	 * @see org.softwarefm.shared.usage.IUsageFromServer#getStatsFor(java.lang.String, org.softwarefm.shared.usage.IUsageFromServerCallback)
	 */
	@Override
	public void getStatsFor(String name, IUsageFromServerCallback callback) {
		IResponse response = client.get(ConfiguratorConstants.userPattern, name).execute();
		if (response.statusCode() == HttpStatus.SC_OK) {
			String raw = response.asString();
			IUsageStats usageStats = persistance.parse(raw);
			callback.foundStats(name, usageStats);
		}

	}

	/* (non-Javadoc)
	 * @see org.softwarefm.shared.usage.IUsageFromServer#getStatsFor(java.util.List, org.softwarefm.shared.usage.IUsageFromServerCallback)
	 */
	@Override
	public void getStatsFor(List<FriendData> friendDatas, IUsageFromServerCallback callback) {
		for (FriendData data : friendDatas) {
			getStatsFor(data.name, callback);
		}
	}

}
