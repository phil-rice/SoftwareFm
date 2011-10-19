package org.softwareFm.mavenExtractor;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.softwareFm.configuration.configurators.DataStoreConfigurator;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.maps.Maps;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings("unused")
public class DependanciesToSling implements IArtifactDependancyVisitor {
	private final IUrlGenerator artifactUrlGenerator;
	private final IRepositoryFacard repository;
	private final int maxCount;
	private int count;

	public DependanciesToSling(IRepositoryFacard repository, int count) {
		this.repository = repository;
		this.maxCount = count;
		GuiDataStore guiDataStore = new GuiDataStore(null, null, ICallback.Utils.rethrow());
		new DataStoreConfigurator().process(guiDataStore);// setsup all the url generator;
		Map<String, IUrlGenerator> urlGeneratorMap = guiDataStore.getUrlGeneratorMap();
		artifactUrlGenerator = urlGeneratorMap.get("urlGenerator.artifact");
	}

	public void vist(String groupid, String artifactid, String childgroupid, String childartifactid) throws Exception {
		String url = artifactUrlGenerator.findUrlFor(makeMap(groupid, artifactid));
		UUID uuid = UUID.randomUUID();
		String fullUrl = url+"/"+"dependancy"+"/"+ uuid;
		Map<String, Object> data = makeMap(childgroupid, childartifactid);
		data.put(MavenImporterConstants.slingResourceTypeKey, "dependancy");
		System.out.println(fullUrl + "<-------" + data);
		repository.post(fullUrl, data, new IResponseCallback.Utils().noCallback()).get();
		if (count++ > maxCount)
			System.exit(0);
	}

	private Map<String, Object> makeMap(String groupid, String artifactid) {
		return Maps.<String, Object> makeMap(MavenImporterConstants.groupIdKey, groupid, MavenImporterConstants.artifactIdKey, artifactid);
	}

	public static void main(String[] args) throws MalformedURLException {
		DataSource dataSource = MavenImporterConstants.dataSource;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		IRepositoryFacard repository = IRepositoryFacard.Utils.defaultFacard();
		new DependancyWalker().walk(MavenImporterConstants.dataSource, new DependanciesToSling(repository, 100), ICallback.Utils.sysErrCallback());
	}

}
