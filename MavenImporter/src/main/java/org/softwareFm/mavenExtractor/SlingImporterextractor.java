package org.softwareFm.mavenExtractor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.configuration.configurators.DataStoreConfigurator;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.jdtBinding.api.JdtConstants;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class SlingImporterextractor implements IExtractorCallback {
	private final GuiDataStore guiDataStore;
	private final IUrlGenerator jarUrlGenerator;
	private final IUrlGenerator versionUrlGenerator;
	private final IUrlGenerator artifactUrlGenerator;
	private final IUrlGenerator groupUrlGenerator;

	private final IRepositoryFacard facard;
	private final File directory;
	private final int maxCount;
	private int count;
	private int postedCount;
	private final List<String> missingProperties = Lists.newList();

	public SlingImporterextractor(File directory, IRepositoryFacard facard, int maxCount) {
		this.directory = directory;
		this.facard = facard;
		this.maxCount = maxCount;
		guiDataStore = new GuiDataStore(null, null, ICallback.Utils.rethrow());
		new DataStoreConfigurator().process(guiDataStore);// setsup all the url generator;
		Map<String, IUrlGenerator> urlGeneratorMap = guiDataStore.getUrlGeneratorMap();
		jarUrlGenerator = urlGeneratorMap.get("urlGenerator.jar");
		versionUrlGenerator = urlGeneratorMap.get("urlGenerator.version");
		artifactUrlGenerator = urlGeneratorMap.get("urlGenerator.artifact");
		groupUrlGenerator = urlGeneratorMap.get("urlGenerator.group");
	}

	public void process(String project, String version, String jarUrl, Model model) throws Exception {
		if (jarUrl.equals(MavenImporterConstants.baseUrl) || jarUrl.length() == 0)
			return;
		if (count++ > maxCount) {
			System.exit(0);
		}
		if (count % 100 == 0)
			System.out.println(count);
		String targetFile = jarUrl.substring(MavenImporterConstants.baseUrl.length());
		File file = new File(directory, targetFile);
		if (file.exists()) {
			String digest = Files.digestAsHexString(file);
			importJar(jarUrl, model, file, digest);
			importVersion(jarUrl, model, file, digest);
			importArtifact(jarUrl, model, file, digest);
			importGroup(model);
			postedCount++;
		}
	}

	private void importJar(String jarUrl, Model model, File file, String digest) throws InterruptedException, ExecutionException {
		String artifactId = model.getArtifactId();
		String groupId = getGroupId(model);
		String version = getVersion(model);
		if (artifactId == null || groupId == null || version == null)
			return;
		Map<String, Object> map = Maps.newMap();
		addMustExist(model, map, MavenImporterConstants.groupIdKey, groupId);
		addMustExist(model, map, MavenImporterConstants.artifactIdKey, artifactId);
		addMustExist(model, map, MavenImporterConstants.versionKey, version);
		addMustExist(model, map, MavenImporterConstants.slingResourceTypeKey, MavenImporterConstants.jarResourceType);
		addFileIfExists(model, map, file, jarUrl, "javadoc", "-javadoc");
		addFileIfExists(model, map, file, jarUrl, "source", "-sources");
		String url = jarUrlGenerator.findUrlFor(Maps.<String, Object> makeMap(JdtConstants.hexDigestKey, digest));
		facard.post(url, map, IResponseCallback.Utils.noCallback()).get();
		}

	private void importGroup(Model model) throws Exception {
		Map<String, Object> map = Maps.newMap();
		String groupId = getGroupId(model);
		addMustExist(model, map, MavenImporterConstants.groupIdKey, groupId);
		addMustExist(model, map, MavenImporterConstants.slingResourceTypeKey, MavenImporterConstants.groupResourceType);
		String url = groupUrlGenerator.findUrlFor(Maps.<String, Object> makeMap(ConfigurationConstants.groupId, groupId));
		facard.post(url, map, IResponseCallback.Utils.noCallback()).get(); // ok so we need a bit more data here...
	}

	private void importArtifact(String jarUrl, Model model, File file, String digest) throws Exception {
		String groupId = getGroupId(model);
		String artifactId = model.getArtifactId();
		Map<String, Object> map = Maps.newMap();
		addIfExists(model, map, MavenImporterConstants.nameKey, model.getName());
		addIfExists(model, map, MavenImporterConstants.descriptionKey, model.getDescription());
		addIfExists(model, map, MavenImporterConstants.projectHomePageKey, model.getUrl());
		addMustExist(model, map, MavenImporterConstants.slingResourceTypeKey, MavenImporterConstants.artifactResourceType);
		// MavenImporterConstants.groupIdKey, groupId);
		IssueManagement issueManagement = model.getIssueManagement();
		if (issueManagement != null)
			addIfExists(model, map, MavenImporterConstants.issueManagementUrlKey, issueManagement.getUrl());
		String url = artifactUrlGenerator.findUrlFor(Maps.<String, Object> makeMap(ConfigurationConstants.groupId, groupId, ConfigurationConstants.artifactId, artifactId));
		facard.post(url, map, IResponseCallback.Utils.noCallback()).get();
	
	}

	private void importVersion(String jarUrl, Model model, File file, String digest) throws InterruptedException, ExecutionException {
		String groupId = getGroupId(model);
		String artifactId = model.getArtifactId();
		String version = getVersion(model);
		Map<String, Object> map = Maps.newMap();
		addMustExist(model, map, MavenImporterConstants.groupIdKey, groupId);
		addMustExist(model, map, MavenImporterConstants.artifactIdKey, artifactId);
		addMustExist(model, map, MavenImporterConstants.versionKey, version);
		addMustExist(model, map, MavenImporterConstants.slingResourceTypeKey, MavenImporterConstants.versionResourceType);
		String url = versionUrlGenerator.findUrlFor(Maps.<String, Object> makeMap(ConfigurationConstants.groupId, groupId, ConfigurationConstants.artifactId, artifactId, ConfigurationConstants.version, version));
		facard.post(url, map, IResponseCallback.Utils.noCallback()).get();
		String digestUrl = url + "/" + digest;
		facard.post(digestUrl, Maps.<String, Object> makeMap(JdtConstants.hexDigestKey, digest, //
				MavenImporterConstants.jarUrl, jarUrl,//
				 MavenImporterConstants.slingResourceTypeKey, MavenImporterConstants.versionJarResourceType,//
				MavenImporterConstants.found, "Spidered from Maven Repository"), IResponseCallback.Utils.noCallback()).get();

	}

	private void addMustExist(Model model, Map<String, Object> map, String key, String value) {
		if (value == null)
			throw new NullPointerException(key);
		addIfExists(model, map, key, value);

	}

	private void addIfExists(Model model, Map<String, Object> map, String key, String value) {
		if (value != null) {
			String deref = deref(model, key, value);
			if (deref != null)
				map.put(key, deref);
		}
	}

	private void addFileIfExists(Model model, Map<String, Object> map, File file, String jarUrl, String key, String suffix) {
		String fileName = DownloadJarsExtractor.append(file.toString(), suffix);
		String url = DownloadJarsExtractor.append(jarUrl, suffix);
		if (new File(fileName).exists())
			addMustExist(model, map, key, url);

	}

	private String deref(Model model, String key, String raw) {
		if (raw.contains("${")) {
			Map<String, String> properties = getModelProperties(model);
			String value = raw;
			for (Entry<String, String> entry : properties.entrySet()) {
				value = value.replace("${" + entry.getKey() + "}", entry.getValue().toString());
			}
			if (value.contains("${")) {
				missingProperties.add(key + ": " + value);
				return null;
			}
			return value;
		}
		return raw;

	}

	private Map<String, String> getModelProperties(Model model) {
		Map<String, String> properties = Maps.newMap();
		String artifactId = model.getArtifactId();
		if (artifactId != null) {
			Maps.putIfNotNull(properties, "artifactId", artifactId);
			Maps.putIfNotNull(properties, "pom.artifactId", artifactId);
			Maps.putIfNotNull(properties, "project.artifactId", artifactId);
			if (artifactId.length() > 8) {
				Maps.putIfNotNull(properties, "project.artifactId.substring(8)", artifactId.substring(8));
				Maps.putIfNotNull(properties, "pom.artifactId.substring(8).toUpperCase()", artifactId.substring(8).toUpperCase());
				Maps.putIfNotNull(properties, "pom.artifactId.substring(8)", artifactId.substring(8));
			}
		}
		Maps.putIfNotNull(properties, "maven.sourceforge.project.groupId", getGroupId(model));
		Maps.putIfNotNull(properties, "project.name", model.getName());
		Maps.putIfNotNull(properties, "pom.name", model.getName());
		Maps.putIfNotNull(properties, "project.version", getVersion(model));
		Parent parent = model.getParent();
		if (parent != null) {
			Maps.putIfNotNull(properties, "project.parent.artifactId", parent.getArtifactId());
			Maps.putIfNotNull(properties, "parent.version", parent.getVersion());
			Maps.putIfNotNull(properties, "parent.groupId", parent.getGroupId());
		}

		if (model.getProperties() != null)
			for (Entry<Object, Object> entry : model.getProperties().entrySet())
				properties.put((String) entry.getKey(), (String) entry.getValue());
		return properties;
	}

	private String getGroupId(Model model) {
		String groupId = model.getGroupId();
		if (groupId != null)
			return groupId;
		Parent parent = model.getParent();
		return parent.getGroupId();
	}

	private String getVersion(Model model) {
		String version = model.getVersion();
		if (version != null)
			return version;
		Parent parent = model.getParent();
		return parent.getVersion();
	}

	public void finished() {
		facard.shutdown();
		System.out.println("posted:" + postedCount);
		System.out.println(Strings.join(missingProperties, "\n"));
		System.out.println("posted:" + postedCount);
	}

	public static void main(String[] args) {
		File directory = new File("c:/softwareFmRepository");
		IRepositoryFacard repository = IRepositoryFacard.Utils.frontEnd("178.79.180.172", 8080, "admin", "admin");
		new ExtractProjectStuff().walk(MavenImporterConstants.dataSource, new SlingImporterextractor(directory, repository, 1000000), ICallback.Utils.sysErrCallback());
	}
}