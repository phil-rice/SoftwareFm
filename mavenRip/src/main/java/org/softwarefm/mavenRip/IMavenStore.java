package org.softwarefm.mavenRip;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.mavenRip.internal.MysqlMavenStore;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.strings.Strings;

public interface IMavenStore {

	public void store(String pomUrl);
	
	public boolean hasPomUrlStartingWith(String prefix);

	public static class Utils {

		public static void store(IMavenStore mavenStore, String host, List<String> pomUrls, ICallback<Throwable> errors) {
			for (String pomUrl : pomUrls) {
				try {
					String fullPomUrl = "http://" + Strings.url(host, pomUrl);
					mavenStore.store(fullPomUrl);
				} catch (Exception e) {
					ICallback.Utils.call(errors, e);
				}
			}
		}

		public static IMavenStore mysqlStore(IMaven maven, DataSource dataSource, boolean sysout) {
			return new MysqlMavenStore(maven, dataSource, sysout);
		}

		public static IMavenStore mysqlStoreWithSysout(BasicDataSource dataSource) {
			return new MysqlMavenStore(IMaven.Utils.makeImport(), dataSource, true);
		}
	}

}
