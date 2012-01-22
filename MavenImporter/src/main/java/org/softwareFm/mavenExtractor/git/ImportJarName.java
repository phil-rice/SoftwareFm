package org.softwareFm.mavenExtractor.git;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.mavenExtractor.IExtractorCallback;
import org.softwareFm.mavenExtractor.MavenImporterConstants;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;
import org.softwareFm.utilities.url.IUrlGenerator;

public class ImportJarName implements IExtractorCallback {
	private final int maxCount;
	private final AtomicInteger count = new AtomicInteger();

	private final IGitFacard gitFacard = IGitFacard.Utils.makeFacard();
	private final File remoteRoot;
	private final IUrlGenerator urlGenerator;

	public ImportJarName(int maxCount, File remoteRoot, IUrlGenerator urlGenerator) {
		this.maxCount = maxCount;
		this.remoteRoot = remoteRoot;
		this.urlGenerator = urlGenerator;
	}

	public void process(String project, String version, String jarUrl, Model model) throws Exception {
		if (count.incrementAndGet() >= maxCount)
			System.exit(0);
		String artifactId = model.getArtifactId();
		String url = urlGenerator.findUrlFor(Maps.stringObjectMap(CollectionConstants.artifactId, artifactId));
		String repoUrl = Strings.allButLastSegment(url, "/");
		File repoDirectory = new File(remoteRoot, repoUrl);
		if (!new File(repoDirectory, ServerConstants.DOT_GIT).exists()) {
			gitFacard.createRepository(remoteRoot, repoUrl);
			System.out.println("Creating: " + repoDirectory);
		}
		File directory = new File(remoteRoot, url);
		directory.mkdirs();
		String data = Json.toString(Maps.stringObjectLinkedMap(CardConstants.group, getGroupId(model), CardConstants.artifact, artifactId, CardConstants.slingResourceType, CardConstants.jarName));
		File file = new File(directory, ServerConstants.dataFileName);
		Files.setText(file, data);
		gitFacard.addAllAndCommit(remoteRoot, url, "Importing jarName");
		System.out.println(repoDirectory + "   " + artifactId + "  " + data);
	}

	public void finished() {
	}

	private String getGroupId(Model model) {
		String groupId = model.getGroupId();
		if (groupId != null)
			return groupId;
		Parent parent = model.getParent();
		return parent.getGroupId();
	}

	public static void main(String[] args) {
		// IRepositoryFacard slingRepository = IRepositoryFacard.Utils.frontEnd("178.79.180.172", 8080, "admin", "admin");
		File home = new File(System.getProperty("user.home"));
		// File localRoot = new File(home, ".sfm");
		final File remoteRoot = new File(home, ".sfm_remote");
		// IRepositoryFacard repository = GitRepositoryFactory.gitLocalRepositoryFacard("localhost", 8080, localRoot, remoteRoot);
		IUrlGenerator urlGenerator = ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap(CardConstants.softwareFmPrefix, CardConstants.dataPrefix).get(CardConstants.jarNameUrlKey);
		new ExtractProjectStuff().walk(MavenImporterConstants.dataSource, //
				new ImportJarName(100000000, remoteRoot, urlGenerator), //
				ICallback.Utils.sysErrCallback());
	}
}