package org.softwareFm.mavenExtractor.git;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.maven.model.Model;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.mavenExtractor.IExtractorCallback;
import org.softwareFm.mavenExtractor.MavenImporterConstants;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.server.GitRepositoryFactory;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.maps.Maps;

public class ImportJarName implements IExtractorCallback {
	private final int maxCount;
	private final AtomicInteger count = new AtomicInteger();

	private final IGitFacard gitFacard = IGitFacard.Utils.makeFacard();
	private final File remoteRoot;
	private final IRepositoryFacard repository;
	private final IUrlGenerator urlGenerator;

	public ImportJarName(int maxCount, File remoteRoot, IRepositoryFacard repository, IUrlGenerator urlGenerator) {
		this.maxCount = maxCount;
		this.remoteRoot = remoteRoot;
		this.repository = repository;
		this.urlGenerator = urlGenerator;
	}

	public void process(String project, String version, String jarUrl, Model model) throws Exception {
		if (count.incrementAndGet() >= maxCount)
			System.exit(0);
		String artifactId = model.getArtifactId();
		String url = urlGenerator.findUrlFor(Maps.stringObjectMap(CardConstants.jarName, artifactId));
		System.out.println(url);
	}

	public void finished() {
	}

	public static void main(String[] args) {
		// IRepositoryFacard slingRepository = IRepositoryFacard.Utils.frontEnd("178.79.180.172", 8080, "admin", "admin");
		File home = new File(System.getProperty("user.home"));
		// File localRoot = new File(home, ".sfm");
		final File remoteRoot = new File(home, ".sfm_remote");
		// IRepositoryFacard repository = GitRepositoryFactory.gitLocalRepositoryFacard("localhost", 8080, localRoot, remoteRoot);
		IRepositoryFacard repository = GitRepositoryFactory.forImport(remoteRoot);
		IUrlGenerator urlGenerator = ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap(CardConstants.softwareFmPrefix, CardConstants.dataPrefix).get(CardConstants.jarNameUrlKey);
		new ExtractProjectStuff().walk(MavenImporterConstants.dataSource, //
				new ImportJarName(10, remoteRoot, repository, urlGenerator), //
				ICallback.Utils.sysErrCallback());
	}
}