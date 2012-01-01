/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.mavenExtractor;

import java.io.File;

import org.apache.maven.model.Model;
import org.softwareFm.mavenExtractor.git.ExtractProjectStuff;
import org.softwareFm.utilities.callbacks.ICallback;

public class PurgeJavadocSourceThatAreIdenticalToJar implements IExtractorCallback {

	private final File directory;
	private int legal;
	private int deleted;

	public PurgeJavadocSourceThatAreIdenticalToJar(File directory) {
		this.directory = directory;
	}

	public void process(String project, String version, String jarUrl, Model model) throws Exception {
		if (jarUrl.equals(MavenImporterConstants.baseUrl) || jarUrl.length() == 0)
			return;
		String targetFile = jarUrl.substring(MavenImporterConstants.baseUrl.length());
		// downloadOneFile(jarUrl, targetFile, "");
		checkOneFile(jarUrl, targetFile, "-sources");
		checkOneFile(jarUrl, targetFile, "-javadoc");
	}

	public void finished() {
		System.out.println("Legal: " + legal);
		System.out.println("Deleted: " + deleted);
	}

	private void checkOneFile(String rawJarUrl, String rawTargetFile, String embed) {
		File jarFile = new File(directory, rawTargetFile);
		File targetFile = new File(directory, DownloadJarsExtractor.append(rawTargetFile, embed));
		if (!jarFile.exists())
			System.err.println("Cannot find: " + jarFile);
		if (targetFile.exists() && rawTargetFile.endsWith(".jar"))
			if ((jarFile.length() == targetFile.length())) {
				System.err.println("Found duplicate: " + targetFile);
				targetFile.delete();
				deleted++;
			} else {
				System.out.println("Found difference: " + targetFile);
				legal++;
			}
	}

	public static void main(String[] args) {
		File directory = new File("c:/softwareFmRepository");
		new ExtractProjectStuff().walk(MavenImporterConstants.dataSource, new PurgeJavadocSourceThatAreIdenticalToJar(directory), ICallback.Utils.sysErrCallback());
	}

}