package org.softwareFm.mavenExtractor;

import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.softwareFm.configuration.configurators.DataStoreConfigurator;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.maps.Maps;
import org.springframework.jdbc.core.JdbcTemplate;

public class MailingListToSling implements IExtractorCallback {
	private final GuiDataStore guiDataStore;
	private final IUrlGenerator artifactUrlGenerator;

	private final IRepositoryFacard facard;
	private final int maxCount;
	private int count;
	private int postedCount;
	private final JdbcTemplate jdbcTemplate;

	public MailingListToSling(IRepositoryFacard facard, DataSource dataSource, int maxCount) {
		this.facard = facard;
		this.maxCount = maxCount;
		guiDataStore = new GuiDataStore(null, null, ICallback.Utils.rethrow());
		new DataStoreConfigurator().process(guiDataStore);// setsup all the url generator;
		Map<String, IUrlGenerator> urlGeneratorMap = guiDataStore.getUrlGeneratorMap();
		artifactUrlGenerator = urlGeneratorMap.get("urlGenerator.artifact");
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void process(String junk1, String junk2, String jarUrl, Model model) throws Exception {
		if (jarUrl.equals(MavenImporterConstants.baseUrl) || jarUrl.length() == 0)
			return;
		if (count++ > maxCount) {
			System.exit(0);
		}
		if (count % 100 == 0)
			System.out.println(count);
		if (!model.getMailingLists().isEmpty()) {
			for (MailingList mailingList : model.getMailingLists()) {
				String groupId = getGroupId(model);
				String artifactId = model.getArtifactId();
				String version = getVersion(model);
				Map<String, Object> urlMap = Maps.newMap();
				addMustExist(model, urlMap, MavenImporterConstants.groupIdKey, groupId);
				addMustExist(model, urlMap, MavenImporterConstants.artifactIdKey, artifactId);
				addMustExist(model, urlMap, MavenImporterConstants.versionKey, version);
				
				Map<String, Object> map = Maps.newMap();
				addMustExist(model, map, MavenImporterConstants.mailingListNameKey, mailingList.getName());
				addIfExists(model, map, MavenImporterConstants.mailingListArchiveKey, mailingList.getArchive());
				map.put( MavenImporterConstants.mailingListOtherArchivesKey, mailingList.getOtherArchives());
				addMustExist(model, map, MavenImporterConstants.mailingListPostKey, mailingList.getPost());
				addIfExists(model, map, MavenImporterConstants.mailingListSubscribeKey, mailingList.getSubscribe());
				addIfExists(model, map, MavenImporterConstants.mailingListUnsubscribeKey, mailingList.getUnsubscribe());
				String url = artifactUrlGenerator.findUrlFor(urlMap);
				System.out.println(url +" <--------- " + map);
				jdbcTemplate.update("insert into mailinglist (groupid,artifactid,version) values(?,?,?)", groupId, artifactId, version);
			}
			postedCount++;
		}
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
	private String deref(Model model, String key, String raw) {
		if (raw.contains("${")) {
			Map<String, String> properties = getModelProperties(model);
			String value = raw;
			for (Entry<String, String> entry : properties.entrySet()) {
				value = value.replace("${" + entry.getKey() + "}", entry.getValue().toString());
			}
			if (value.contains("${")) {
				return null;
			}
			return value;
		}
		return raw;

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
		System.out.println("posted:" + postedCount);
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
	public static void main(String[] args) {
		IRepositoryFacard repository = IRepositoryFacard.Utils.frontEnd("178.79.180.172", 8080, "admin", "admin");
		new ExtractProjectStuff().walk(MavenImporterConstants.dataSource, new MailingListToSling(repository, MavenImporterConstants.dataSource, 10), ICallback.Utils.sysErrCallback());
	}
}
