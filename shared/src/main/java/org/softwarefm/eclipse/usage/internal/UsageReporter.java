package org.softwarefm.eclipse.usage.internal;

import org.softwarefm.eclipse.usage.IUsagePersistance;
import org.softwarefm.eclipse.usage.IUsageReporter;
import org.softwarefm.eclipse.usage.IUsageStats;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.strings.Strings;

public class UsageReporter implements IUsageReporter {

	private final IUsagePersistance persistance;
	private final IHttpClient client;

	public UsageReporter(IUsagePersistance persistance, String host, int port, String url) {
		this.persistance = persistance;
		client = IHttpClient.Utils.builder().host(host, port);
	}

	public void report(String user, IUsageStats usageStats) {
		try {
			String text = persistance.save(usageStats);
			byte[] zipped = Strings.zip(text);
			Strings.unzip(zipped); //just check it unzips
			client.post("usage/" + user).withEntity(zipped).execute();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
