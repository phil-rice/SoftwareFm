/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.repository.api.impl;

import static org.softwareFm.repository.api.RepositoryDataItemStatus.FOUND;
import static org.softwareFm.repository.api.RepositoryDataItemStatus.NOT_FOUND;
import static org.softwareFm.repository.api.RepositoryDataItemStatus.REQUESTED;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.jdtBinding.api.IJarDigester;
import org.softwareFm.repository.api.IRepositoryStatusListener;
import org.softwareFm.repository.api.ISoftwareFmRepository;
import org.softwareFm.repository.api.MemoryStatusChangedListener;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.tests.INeedsServerTest;

public class SoftwareFmRepositoryTest extends TestCase implements INeedsServerTest {

	private final String organisationUrlKey = "organisation.url";
	private final String organisationNameKey = "organisation.name";
	private final String descriptionKey = "description";

	private ISoftwareFmRepository repository;
	private IRepositoryFacard facard;
	private IJarDigester jarDigestor;

	@Test
	public void testGetAndModifyData() throws Exception {
		String url = "/tests/" + getClass().getSimpleName();
		facard.delete(url, IResponseCallback.Utils.memoryCallback()).get();
		checkModifyAndGetData(url, organisationUrlKey, "orgUrl", Maps.<String, Object> makeMap(//
				organisationUrlKey, "orgUrl",//
				"jcr:primaryType", "nt:unstructured"));
		checkModifyAndGetData(url, organisationNameKey, "orgName", Maps.<String, Object> makeMap(//
				organisationUrlKey, "orgUrl",//
				"jcr:primaryType", "nt:unstructured",//
				organisationNameKey, "orgName"));
		checkModifyAndGetData(url, descriptionKey, "orgDesc", Maps.<String, Object> makeMap(//
				organisationUrlKey, "orgUrl",//
				organisationNameKey, "orgName",//
				"jcr:primaryType", "nt:unstructured",//
				descriptionKey, "orgDesc"));
	}

	@SuppressWarnings("unchecked")
	private void checkModifyAndGetData(String url, String key, Object value, Map<String, Object> expected) throws Exception {
		Map<String, Object> context1 = Maps.makeMap("c", 1);
		Map<String, Object> context2 = Maps.makeMap("c", 2);
		// Map<String, Object> entityDefn = Maps.makeMap();
		String entity = "entity";
		Map<String, Object> actionGet = Maps.makeMap(RepositoryConstants.action, RepositoryConstants.actionGet, RepositoryConstants.entity, entity);
		Map<String, Object> actionPost = Maps.makeMap(RepositoryConstants.action, RepositoryConstants.actionPost, RepositoryConstants.entity, entity);

		MemoryStatusChangedListener validListener = IRepositoryStatusListener.Utils.memory();
		repository.addStatusListener(validListener);
		repository.modifyData(entity, url, key, value, context1).get();
		validListener.assertEquals(//
				url, REQUESTED, null, Maps.merge(context1, actionPost), //
				url, FOUND, expected, Maps.merge(context1, actionPost));
		repository.getData(entity, url, context2).get();
		validListener.assertEquals(//
				url, REQUESTED, null, Maps.merge(context1, actionPost),//
				url, FOUND, expected, Maps.merge(context1, actionPost),//
				url, REQUESTED, null, Maps.merge(context2, actionGet),//
				url, FOUND, expected, Maps.merge(context2, actionGet));

	}

	@SuppressWarnings("unchecked")
	public void testNotifyListenersThereIsNoData() {
		String entity = "someEntity";
		Map<String, Object> actionNotifyNoData = Maps.makeMap(RepositoryConstants.action, RepositoryConstants.actionNotifyNoData, RepositoryConstants.entity, entity);

		MemoryStatusChangedListener validListener1 = IRepositoryStatusListener.Utils.memory();
		MemoryStatusChangedListener validListener2 = IRepositoryStatusListener.Utils.memory();
		repository.addStatusListener(validListener1);
		repository.addStatusListener(validListener2);

		Map<String, Object> context = Maps.makeMap("a", 1, "b", 2);
		repository.notifyListenersThereIsNoData(entity, context);

		validListener1.assertEquals("", NOT_FOUND, null, Maps.<String, Object> merge(context, actionNotifyNoData));
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		facard = IRepositoryFacard.Utils.defaultFacard();
		jarDigestor = IJarDigester.Utils.digester();
		repository = ISoftwareFmRepository.Utils.repository(facard, jarDigestor);
	}
}