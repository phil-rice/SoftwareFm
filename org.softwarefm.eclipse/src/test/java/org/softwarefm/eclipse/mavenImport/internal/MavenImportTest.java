package org.softwarefm.eclipse.mavenImport.internal;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.maven.model.Model;
import org.softwarefm.eclipse.maven.IMaven;
import org.softwarefm.eclipse.maven.internal.Maven;
import org.softwarefm.utilities.collections.Files;

public class MavenImportTest extends TestCase {

	private File userHomeDir;
	private File userHomeM2Dir;

	public void testModelToJarUrl() throws Exception {
		checkModelToJarUrl("pomNoDistributionRepo.xml", "http://repo1.maven.org/maven2/org/softwarefm/eclipse/0.0.1-SNAPSHOT/eclipse-0.0.1-SNAPSHOT.jar");
		checkModelToJarUrl("pomWithDistributionRepo.xml", "https://repository.mycompany.com/repository/maven2/com/mycompany/app/my-app/1.0-SNAPSHOT/my-app-1.0-SNAPSHOT.jar");
		checkModelToJarUrl("pomWithOnlyArtifactSpecified.xml", "http://repo1.maven.org/maven2/org/softwarefm/eclipse/0.0.1-SNAPSHOT/eclipse-0.0.1-SNAPSHOT.jar");
	}

	public void testModelToUrlWhenMalformedRepositorySpoec() throws Exception {
		checkModelToJarUrl("pomWithUnknownProtocolInDistributionRepo.xml", "http://repo1.maven.org/maven2/com/mycompany/app/my-app/1.0-SNAPSHOT/my-app-1.0-SNAPSHOT.jar");
	}

	public void testJarFileName() throws Exception {
		checkJarFileName(userHomeM2Dir, "pomNoDistributionRepo.xml", new File(userHomeM2Dir, "org/softwarefm/eclipse/0.0.1-SNAPSHOT/eclipse-0.0.1-SNAPSHOT.jar"));
		checkJarFileName(new File("x"), "pomNoDistributionRepo.xml", new File("x/org/softwarefm/eclipse/0.0.1-SNAPSHOT/eclipse-0.0.1-SNAPSHOT.jar"));
	}

	public void testDownloadJar() throws Exception {
		File sourceFile = new File("src/test/resources/repo/org/softwarefm/test-artifact/1.0/test-artifact-1.0.jar");
		File destinationFile = new File(userHomeM2Dir, "org/softwarefm/test-artifact/1.0/test-artifact-1.0.jar");
		destinationFile.delete();
		Model model = getModel("pomTestArtifact.xml");
		Maven maven = new Maven();
		assertEquals(new URL("file:" + sourceFile), maven.jarUrl(model)); // just checking setup
		File actual = maven.downloadJar(model);
		assertEquals(destinationFile,actual);
		assertTrue(destinationFile.exists());
		assertEquals(Files.digestAsHexString(sourceFile), Files.digestAsHexString(destinationFile));

	}

	private void checkModelToJarUrl(String pom, String expected) throws Exception {
		final IMaven maven = new Maven();
		Model model = getModel(pom);
		URL actual = maven.jarUrl(model);
		assertEquals(new URL(expected), actual);
	}

	private Model getModel(String pom) throws Exception {
		final IMaven maven = new Maven();
		String pomMod = getClass().getPackage().getName().replace(".", "/") + "/" + pom;
		String pomUrl = getClass().getClassLoader().getResource(pomMod).toExternalForm();
		Model model = maven.pomToModel(pomUrl);
		return model;
	}

	private void checkJarFileName(File m2Home, String pom, File expected) throws Exception {
		final IMaven maven = new Maven(m2Home);
		Model model = getModel(pom);
		File actual = maven.jarFile(model);
		assertEquals(expected, actual);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userHomeDir = new File(System.getProperty("user.home"));
		userHomeM2Dir = new File(userHomeDir, ".m2");
	}

}
