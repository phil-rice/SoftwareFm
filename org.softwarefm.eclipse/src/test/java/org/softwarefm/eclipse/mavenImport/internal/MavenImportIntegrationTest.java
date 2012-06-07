package org.softwarefm.eclipse.mavenImport.internal;

import java.io.File;

import junit.framework.TestCase;

import org.apache.maven.model.Model;
import org.softwarefm.eclipse.maven.internal.Maven;
import org.softwarefm.utilities.annotations.IntegrationTest;
import org.softwarefm.utilities.collections.Files;

@IntegrationTest
public class MavenImportIntegrationTest extends TestCase {

	public void test_PomFileSpecified_NoRepositorySpecified() throws Exception {
		checkRealImport("http://repo1.maven.org/maven2/org/jboss/resteasy/resteasy-jaxrs/2.3.3.Final/resteasy-jaxrs-2.3.3.Final.pom", "5413dfcccd263bbf44ad68f9d10e1fef47a4d39d");
	}

	public void test_PomFileSpecified_RepositorySpecified() throws Exception {
		checkRealImport("http://repo1.maven.org/maven2/maven/maven-jdiff-plugin/1.5.1/maven-jdiff-plugin-1.5.1.pom", "13d962f3239ed75676b166304b3c25868e25c9bd");
	}

	public void _testWhenPomOkButNoJar() throws Exception {
		fail("Rewrite so that it expects a sensible error message");
		checkRealImport("http://repo1.maven.org/maven2/org/apache/maven/wagon/wagon/2.2/wagon-2.2.pom", "");
	}

	private void checkRealImport(String pomUrl, String expected) throws Exception {
		Maven maven = new Maven();
		Model model = maven.pomToModel(pomUrl);
		File jarFile = maven.jarFile(model);
		jarFile.delete();

		assertEquals(expected, Files.digestAsHexString(maven.downloadJar(model)));
		assertEquals(expected, Files.digestAsHexString(maven.downloadJar(model))); // this time already loaded

	}

}
