package org.softwarefm.mavenRip;

import javax.sql.DataSource;

import org.softwarefm.mavenRip.internal.MavenWalker;
import org.softwarefm.utilities.callbacks.ICallback;

public interface IMavenWalker {

	void walk(IGroupArtifactVisitor visitor);
	
	public static class Utils{
		public static IMavenWalker walker(DataSource dataSource, ICallback<Throwable> errorCallback){
			return new MavenWalker(dataSource, errorCallback);
		}
	}
	
}
