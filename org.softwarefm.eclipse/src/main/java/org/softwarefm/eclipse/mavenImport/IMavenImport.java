package org.softwarefm.eclipse.mavenImport;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.model.Model;
import org.softwarefm.eclipse.mavenImport.internal.MavenImport;
import org.softwarefm.utilities.callbacks.ICallback;

public interface IMavenImport {

	Model pomToModel(String pomUrl) throws Exception;

	File downloadJar(Model model) throws Exception;

	URL jarUrl(Model model) throws MalformedURLException;

	File jarFile(Model model);

	public static class Utils {
		public static IMavenImport makeImport() {
			return new MavenImport();
		}

		public static IMavenImport makeImport(File m2Home) {
			return new MavenImport(m2Home);
		}

		public static ICallback<String> importPomWithSysouts() {
			final MavenImport mavenImport = new MavenImport();
			return new ICallback<String>() {
				public void process(String pomUrl) throws Exception {
					System.out.println("Downloading pom: " + pomUrl);
					Model model = mavenImport.pomToModel(pomUrl);
					System.out.println("Loaded pom");
					System.out.println("Jar url: " + mavenImport.jarUrl(model));
					System.out.println("Target: " + mavenImport.jarFile(model));
					mavenImport.downloadJar(model);
					System.out.println("Imported");
				}
			};
		}
	}

}
