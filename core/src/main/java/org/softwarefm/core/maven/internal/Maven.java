package org.softwarefm.core.maven.internal;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.strings.Strings;

public class Maven implements IMaven {

	private final File m2Home;

	public Maven() {
		this(getM2Home());
	}

	public Maven(File m2Home) {
		this.m2Home = m2Home;
	}

	private static File getM2Home() {
		String m2HomeProperty = System.getProperty("M2_HOME");
		if (m2HomeProperty == null)
			return new File(new File(System.getProperty("user.home")), ".m2");
		else
			return new File(m2HomeProperty);
	}

	public Model pomToModel(String pomUrl)  {
		try {
			String realPomUrl = getRealPomUrl(pomUrl);
			URL url = new URL(realPomUrl);
			InputStream stream = url.openStream();
			try {
				Model model = new MavenXpp3Reader().read(stream);
				return model;
			} finally {
				stream.close();
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private String getRealPomUrl(String pomUrl) {
		if (pomUrl.endsWith("pom"))
			return pomUrl;
		List<String> segments = Strings.splitIgnoreBlanks(pomUrl, "/");
		if (segments.size() != 6)
			return pomUrl;
		// String protocol = segments.get(0);
		String base = segments.get(1);
		if (!base.equals("mvnrepository.com"))
			return pomUrl;
		String marker = segments.get(2);
		if (!marker.equals("artifact"))
			return pomUrl;
		String groupId = segments.get(3);
		String artifactId = segments.get(4);
		String version = segments.get(5);
		return IMaven.Utils.makePomUrlForMvnRepository(groupId, artifactId, version);
	}

	public URL jarUrl(Model model)  {
		try {
			return getJarUrl(model, true);
		} catch (Exception e) {
			try {
				return getJarUrl(model, false);
			} catch (MalformedURLException e1) {
				throw WrappedException.wrap(e1);
			}
		}
	}

	private URL getJarUrl(Model model, boolean useStatedRepo) throws MalformedURLException {
		String repoUrl = getRepoUrl(model, useStatedRepo);
		String urlString = MessageFormat.format(repoUrl + "/{0}/{1}/{2}/{1}-{2}.jar", IMaven.Utils.getGroupId(model).replace(".", "/"), IMaven.Utils.getArtifactId(model), IMaven.Utils.getVersion(model));
		return new URL(urlString);
	}

	private String getRepoUrl(Model model, boolean useStatedRepo) {
		if (useStatedRepo) {
			DistributionManagement distributionManagement = model.getDistributionManagement();
			if (distributionManagement != null) {
				DeploymentRepository repository = distributionManagement.getRepository();
				repository.getLocation("");
				if (repository != null) {
					String repoUrl = repository.getUrl();
					return repoUrl;
				}
			}
		}
		return "http://repo1.maven.org/maven2";
	}

	public File jarFile(Model model) {
		String groupId = IMaven.Utils.getGroupId(model);
		String artifactId = IMaven.Utils.getArtifactId(model);
		String version = IMaven.Utils.getVersion(model);
		File result = new File(m2Home, groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar");
		return result;
	}

	public File downloadJar(Model model)  {
		URL url = jarUrl(model);
		File file = jarFile(model);
		Files.downLoadFile(url, file);
		return file;
	}

	@Override
	public String artifactId(Model model) {
		return model.getArtifactId();
	}

	@Override
	public String groupId(Model model) {
		String raw = model.getGroupId();
		if (raw == null)
			return model.getParent().getGroupId();
		else
			return raw;
	}

	@Override
	public String version(Model model) {
		String raw = model.getVersion();
		if (raw == null)
			return model.getParent().getVersion();
		else
			return raw;
	}
	
	public static void main(String[] args) throws Exception {
		String pomUrl = "http://repo1.maven.org/maven2/org/apache/maven/maven-model-v3/2.0/maven-model-v3-2.0.pom";
		Maven maven = new Maven();
		System.out.println(pomUrl);
		Model model = maven.pomToModel(pomUrl);
		System.out.println(model);
		System.out.println(maven.jarUrl(model));
		File jarFile = maven.jarFile(model);
		System.out.println(jarFile);
		maven.downloadJar(model);
		System.out.println(Files.digestAsHexString(jarFile));
		System.out.println("Done");
	}

}
