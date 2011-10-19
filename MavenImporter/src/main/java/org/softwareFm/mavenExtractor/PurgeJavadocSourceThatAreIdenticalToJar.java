package org.softwareFm.mavenExtractor;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.maven.model.Model;
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
		if (targetFile.exists()&& rawTargetFile.endsWith(".jar"))
			if ((jarFile.length() == targetFile.length()) ) {
				System.err.println("Found duplicate: " + targetFile);
				targetFile.delete();
				deleted++;
			} else {
				System.out.println("Found difference: " + targetFile);
				legal++;
			}
	}

	public static void main(String[] args) throws MalformedURLException {
		File directory = new File("c:/softwareFmRepository");
		new ExtractProjectStuff().walk(MavenImporterConstants.dataSource, new PurgeJavadocSourceThatAreIdenticalToJar(directory), ICallback.Utils.sysErrCallback());
	}

}
