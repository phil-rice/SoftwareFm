package org.softwareFm.swt.unrecognisedJar;

import java.io.File;

public class UnrecognisedJarData {
	public static UnrecognisedJarData forTests(String projectName, File jarFile) {
		return new UnrecognisedJarData(projectName, jarFile);
	}

	public final String projectName;
	public final File jarFile;

	private UnrecognisedJarData(String projectName, File jarFile) {
		this.projectName = projectName;
		this.jarFile = jarFile;

	}

	@Override
	public String toString() {
		return "UnrecognisedJarData [projectName=" + projectName + ", jarFile=" + jarFile + "]";
	}


}
