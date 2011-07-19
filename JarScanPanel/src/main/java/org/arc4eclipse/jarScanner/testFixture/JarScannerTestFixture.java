package org.arc4eclipse.jarScanner.testFixture;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.xml.DOMConfigurator;
import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.repositoryClient.api.IJarDetails;
import org.arc4eclipse.repositoryClient.api.IRepositoryClient;
import org.arc4eclipse.repositoryClient.paths.IPathCalculator;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.arc4eclipse.utilities.collections.Files;
import org.arc4eclipse.utilities.maps.Maps;
import org.springframework.core.io.ClassPathResource;

public class JarScannerTestFixture {

	private final static Map<String, Map<String, Integer>> jarToRelease = Maps.makeMap(//
			"commons-math-2.0.jar", Maps.makeMap(//
					JarScannerConstants.organisation, "http://www.apache.org/",//
					JarScannerConstants.project, "commons.apache.org/math/",//
					JarScannerConstants.release, "2.0"),//
			"commons-math-2.0.jar", Maps.makeMap(//
					JarScannerConstants.organisation, "http://www.apache.org/",//
					JarScannerConstants.project, "commons.apache.org/math/",//
					JarScannerConstants.release, "2.0"));//

	private final static IRepositoryFacard<Object, IJarDetails, IEntityType, Map<Object, Object>> repositoryFacard = IRepositoryFacard.Utils.facard(IRepositoryClient.Utils.repositoryClient(new IPathCalculator<IJarDetails, IEntityType>() {
		@Override
		public String makeUrl(IJarDetails jarDetails, IEntityType entityType) {
			String jarPrefix = "/jar/";
			switch (entityType) {
			case PROJECT:
				return jarPrefix + jarDetails.digestAsHexString() + "/project";
			case RELEASE:
				return jarPrefix + jarDetails.digestAsHexString() + "/release";
			default:
				throw new IllegalArgumentException("Not implemented for: " + entityType);
			}
		}
	}, IHttpClient.Utils.defaultClient()));

	public static void main(String[] args) throws IOException {
		DOMConfigurator.configure(new ClassPathResource("log4j.xml").getURL());
		IRepositoryFacardCallback<Object, IJarDetails, IEntityType, Map<Object, Object>> callback = IRepositoryFacardCallback.Utils.sysoutDoneCallback();

		File directory = new ClassPathResource("Marker.txt").getFile().getParentFile();
		for (File jarFile : directory.listFiles(Files.extensionFilter("jar"))) {
			IJarDetails rawJarDetails = IJarDetails.Utils.makeJarDetails(jarFile, null);
			IJarDetails withDigest = IJarDetails.Utils.withDigest(rawJarDetails);
			String digest = withDigest.digestAsHexString();
			Map<Object, Object> data = Maps.makeMap(//
					JarScannerConstants.jarName, withDigest.shortJarName(), //
					JarScannerConstants.digest, withDigest.digestAsHexString());
			Map<String, Integer> organisation = jarToRelease.get(withDigest.shortJarName());
			if (organisation != null)
				data.put(JarScannerConstants.organisation, organisation);
			repositoryFacard.setDetails(digest, withDigest, IEntityType.RELEASE, data, callback);
		}
		System.out.println("Reading");
		for (File jarFile : directory.listFiles(Files.extensionFilter("jar"))) {
			IJarDetails rawJarDetails = IJarDetails.Utils.makeJarDetails(jarFile, null);
			IJarDetails withDigest = IJarDetails.Utils.withDigest(rawJarDetails);
			String digest = withDigest.digestAsHexString();
			repositoryFacard.getDetails(digest, withDigest, IEntityType.RELEASE, callback);
		}

	}
}
