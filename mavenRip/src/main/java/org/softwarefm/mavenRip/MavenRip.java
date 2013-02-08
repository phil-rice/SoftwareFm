package org.softwarefm.mavenRip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;
import org.softwarefm.utilities.strings.Strings;

public class MavenRip {
	public final static BasicDataSource dataSource = new BasicDataSource();
	static {
		dataSource.setUrl("jdbc:mysql://localhost:3306/mavenrip");
		dataSource.setUsername("root");
		dataSource.setPassword("iwtbde");
	}

	private final String host;
	private final IHttpClient client;
	private final IMavenStore mavenStore;
	private final ICallback<Throwable> errorCallback;

	public MavenRip(String host, int port, IMavenStore mavenStore, ICallback<Throwable> errorCallback) {
		this.host = host;
		this.mavenStore = mavenStore;
		this.errorCallback = errorCallback;
		this.client = IHttpClient.Utils.builder().host(host, port);
	}

	public List<String> listedItems(String url) {
		IResponse response = client.get(url).execute();
		String table = Strings.findItem(response.asString(), "<tbody>", "</tbody>");
		AtomicInteger index = new AtomicInteger();
		List<String> result = new ArrayList<String>();
		while (true) {
			String name = Strings.findItem(table, "<a href=\"", "\">", index);
			if (name == null)
				return result;
			result.add(name);
		}
	}

	public List<String> childDirectorys(List<String> listedItems) {
		List<String> result = new ArrayList<String>();
		for (String item : listedItems)
			if (item.endsWith("/"))
				if (!item.startsWith("."))
					result.add(item);
		return result;
	}

	public List<String> pomUrls(String url, List<String> listedItems) {
		List<String> result = new ArrayList<String>();
		for (String item : listedItems)
			if (item.endsWith(".pom"))
				result.add(Strings.url(url, item));
		return result;
	}

	public void ripUrlAndBelow(String url) {
		List<String> childDirectorys = ripDirectoryFindChildren(url);
		for (String child : childDirectorys)
			ripUrlAndBelow(Strings.url(url, child));

	}

	private List<String> ripDirectoryFindChildren(String url) {
		List<String> listedItems = Collections.emptyList();
		try {
			listedItems = listedItems(url);
			List<String> poms = pomUrls(url, listedItems);
			IMavenStore.Utils.store(mavenStore, host, poms, errorCallback);
			return childDirectorys(listedItems);
		} catch (Exception e) {
			ICallback.Utils.call(errorCallback, e);
			return listedItems;
		}
	}

	/** Pretty rubbish filter that skips over a directory if have already anything from it. */
	public void ripUrlAndBelowWhenNotPresent(String url) {
		List<String> childDirectorys = ripDirectoryFindChildren(url);
		for (String child : childDirectorys) {
			String childUrl = "http://" +Strings.url(host, url, child);
			boolean present = mavenStore.hasPomUrlStartingWith(childUrl);
			System.out.println("Checking: " + childUrl +", "  + present);
			if (!present)
				ripUrlAndBelow(Strings.url(url, child));
		}

	}

	public static void main(String[] args) {
		String host = "mirrors.ibiblio.org";
		String url = "maven2";

		IMavenStore mavenStore = IMavenStore.Utils.mysqlStoreWithSysout(dataSource);
		ICallback<Throwable> errorCallback = ICallback.Utils.sysErrCallback();
		MavenRip mavenRip = new MavenRip(host, 80, mavenStore, errorCallback);
		// mavenRip.ripUrlAndBelow(url);
		mavenRip.ripUrlAndBelowWhenNotPresent(url);
	}
}
