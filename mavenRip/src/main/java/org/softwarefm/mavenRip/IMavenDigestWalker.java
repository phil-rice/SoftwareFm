package org.softwarefm.mavenRip;

import javax.sql.DataSource;

import org.softwarefm.core.maven.IMaven;
import org.softwarefm.mavenRip.internal.HighestVersionDigestWalker;
import org.softwarefm.utilities.callbacks.ICallback;

public interface IMavenDigestWalker {

	void walk(IMavenDigestVistor visitor);
	
	public static class Utils{
		IMavenDigestWalker digestWalker(IMaven maven, DataSource dataSource, ICallback<Throwable> errorCallback, boolean onlyVisitNew){
			return new HighestVersionDigestWalker(maven, dataSource, errorCallback, onlyVisitNew);
			
		}
	}
}
