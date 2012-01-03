package org.softwareFm.mavenExtractor.gash;

import java.io.File;

public class Delete {

	public static void main(String[] args) {
		// IRepositoryFacard slingRepository = IRepositoryFacard.Utils.frontEnd("178.79.180.172", 8080, "admin", "admin");
		File home = new File(System.getProperty("user.home"));
		// File localRoot = new File(home, ".sfm");
		final File remoteRoot = new File(home, ".sfm_remote");
		File jarNames = new File(remoteRoot, "softwareFm/jarname");
		System.out.println(jarNames);
//		Files.deleteDirectory(jarNames);
	}
}
