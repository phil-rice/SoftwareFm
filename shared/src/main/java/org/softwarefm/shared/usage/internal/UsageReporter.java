package org.softwarefm.shared.usage.internal;

import org.softwarefm.server.configurator.ConfiguratorConstants;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageReporter;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;
import org.softwarefm.utilities.strings.Strings;

public class UsageReporter implements IUsageReporter {

	private final IUsagePersistance persistance;
	private final IHttpClient client;

	public UsageReporter(IUsagePersistance persistance, IHttpClient httpClientWithUrl) {
		this.persistance = persistance;
		client = httpClientWithUrl;
	}

	public void report(String user, IUsageStats usageStats) {
		try {
			String text = persistance.saveUsageStats(usageStats);
			byte[] zipped = Strings.zip(text);
			Strings.unzip(zipped); // just check it unzips
			System.out.println("Posting");
			IResponse response = client.post(ConfiguratorConstants.userPattern, user).withEntity(zipped).execute();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
