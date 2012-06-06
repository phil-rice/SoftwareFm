package org.softwarefm.eclipse.mavenImport.internal;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.softwarefm.eclipse.mavenImport.IMavenImport;
import org.softwarefm.utilities.annotations.IntegrationTest;
import org.softwarefm.utilities.collections.Files;

@IntegrationTest
public class MavenImport implements IMavenImport {

	private final File m2Home;

	public MavenImport() {
		this(getM2Home());
	}

	public MavenImport(File m2Home) {
		this.m2Home = m2Home;
	}

	private static File getM2Home() {
		String m2HomeProperty = System.getProperty("M2_HOME");
		if (m2HomeProperty == null)
			return new File(new File(System.getProperty("user.home")), ".m2");
		else
			return new File(m2HomeProperty);
	}

	public Model pomToModel(String pomUrl) throws Exception {
		URL url = new URL(pomUrl);
		InputStream stream = url.openStream();
		try {
			Model model = new MavenXpp3Reader().read(stream);
			return model;
		} finally {
			stream.close();
		}
	}

	public URL jarUrl(Model model) throws MalformedURLException {
		String repoUrl = getRepoUrl(model);
		String urlString = MessageFormat.format(repoUrl + "/{0}/{1}/{2}/{1}-{2}.jar", getGroupId(model).replace(".", "/"), model.getArtifactId(), model.getVersion());
		return new URL(urlString);

	}

	private String getRepoUrl(Model model) {
		DistributionManagement distributionManagement = model.getDistributionManagement();
		if (distributionManagement != null) {
			DeploymentRepository repository = distributionManagement.getRepository();
			if (repository != null) {
				String repoUrl = repository.getUrl();
				return repoUrl;
			}
		}
		return "http://repo1.maven.org/maven2";
	}

	public File jarFile(Model model) {
		File result = new File(m2Home, getGroupId(model).replace(".", "/") + "/" + model.getArtifactId() + "/" + model.getVersion() + "/" + model.getArtifactId() + "-" + model.getVersion() + ".jar");
		return result;
	}

	public File downloadJar(Model model) throws Exception {
		URL url = jarUrl(model);
		File file = jarFile(model);
		Files.downLoadFile(url, file);
		return file;
	}

	private String getGroupId(Model model) {
		String groupId = model.getGroupId();
		if (groupId != null)
			return groupId;
		Parent parent = model.getParent();
		return parent.getGroupId();
	}

	public static void main(String[] args) throws Exception {
		String pomUrl = "http://repo1.maven.org/maven2/org/apache/maven/maven-model-v3/2.0/maven-model-v3-2.0.pom";
		MavenImport mavenImport = new MavenImport();
		System.out.println(pomUrl);
		Model model = mavenImport.pomToModel(pomUrl);
		System.out.println(model);
		System.out.println(mavenImport.jarUrl(model));
		File jarFile = mavenImport.jarFile(model);
		System.out.println(jarFile);
		mavenImport.downloadJar(model);
		System.out.println(Files.digestAsHexString(jarFile));
		System.out.println("Done");
	}

}
