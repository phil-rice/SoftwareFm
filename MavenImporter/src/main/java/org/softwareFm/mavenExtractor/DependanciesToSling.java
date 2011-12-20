/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.mavenExtractor;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.maps.Maps;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings("unused")
public class DependanciesToSling implements IArtifactDependancyVisitor {
	private IUrlGenerator artifactUrlGenerator;
	private final IRepositoryFacard repository;
	private final int maxCount;
	private int count;

	public DependanciesToSling(IRepositoryFacard repository, int count) {
		this.repository = repository;
		this.maxCount = count;
////		GuiDataStore guiDataStore = new GuiDataStore(null, null, ICallback.Utils.rethrow());
////		new DataStoreConfigurator().process(guiDataStore);// setsup all the url generator;
//		Map<String, IUrlGenerator> urlGeneratorMap = guiDataStore.getUrlGeneratorMap();
//		artifactUrlGenerator = urlGeneratorMap.get("urlGenerator.artifact");
	}

	public void vist(String groupid, String artifactid, String childgroupid, String childartifactid) throws Exception {
		String url = artifactUrlGenerator.findUrlFor(makeMap(groupid, artifactid));
		UUID uuid = UUID.randomUUID();
		String fullUrl = url + "/" + "dependancy" + "/" + uuid;
		Map<String, Object> data = makeMap(childgroupid, childartifactid);
		data.put(MavenImporterConstants.slingResourceTypeKey, "dependancy");
		System.out.println(fullUrl + "<-------" + data);
		repository.post(fullUrl, data,  IResponseCallback.Utils.noCallback()).get();
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