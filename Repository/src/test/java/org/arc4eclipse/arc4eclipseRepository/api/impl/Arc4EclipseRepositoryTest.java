package org.arc4eclipse.arc4eclipseRepository.api.impl;

import static org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.MemoryStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.jdtBinding.api.IJarDigester;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.utilities.maps.Maps;
import org.junit.Before;
import org.junit.Test;

public class Arc4EclipseRepositoryTest extends TestCase {

	private IArc4EclipseRepository repository;

	private String digest;
	// private Resource classWorldsDigest;

	private UrlGenerator urlGenerator;

	private IRepositoryFacard facard;

	private IJarDigester jarDigestor;

	@Test
	public void testGetAndModifyJarData() throws Exception {
		facard.delete("/" + urlGenerator.forJar().apply(digest), IResponseCallback.Utils.memoryCallback()).get();
		checkModifyAndGetJarData("name1", "value1", Maps.<String, Object> makeMap(//
				RepositoryConstants.hexDigestKey, digest, //
				"jcr:primaryType", "nt:unstructured",//
				"name1", "value1"));

		checkModifyAndGetJarData(RepositoryConstants.organisationUrlKey, "OrgUrl", Maps.<String, Object> makeMap(//
				RepositoryConstants.hexDigestKey, digest, //
				RepositoryConstants.organisationUrlKey, "OrgUrl",//
				"jcr:primaryType", "nt:unstructured",//
				"name1", "value1"));

		checkModifyAndGetJarData(RepositoryConstants.projectUrlKey, "ProjName", Maps.<String, Object> makeMap(//
				RepositoryConstants.hexDigestKey, digest, //
				RepositoryConstants.organisationUrlKey, "OrgUrl",//
				RepositoryConstants.projectUrlKey, "ProjName",//
				"jcr:primaryType", "nt:unstructured",//
				"name1", "value1"));
	}

	@Test
	public void testGetAndModifyData() throws Exception {
		String url = "/tests/" + getClass().getSimpleName();
		facard.delete(url, IResponseCallback.Utils.memoryCallback()).get();
		checkModifyAndGetData(url, RepositoryConstants.organisationUrlKey, "orgUrl", Maps.<String, Object> makeMap(//
				RepositoryConstants.organisationUrlKey, "orgUrl",//
				"jcr:primaryType", "nt:unstructured"));
		checkModifyAndGetData(url, RepositoryConstants.organisationNameKey, "orgName", Maps.<String, Object> makeMap(//
				RepositoryConstants.organisationUrlKey, "orgUrl",//
				"jcr:primaryType", "nt:unstructured",//
				RepositoryConstants.organisationNameKey, "orgName"));
		checkModifyAndGetData(url, RepositoryConstants.descriptionKey, "orgDesc", Maps.<String, Object> makeMap(//
				RepositoryConstants.organisationUrlKey, "orgUrl",//
				RepositoryConstants.organisationNameKey, "orgName",//
				"jcr:primaryType", "nt:unstructured",//
				RepositoryConstants.descriptionKey, "orgDesc"));
	}

	public void testGetAndModifyDataWithListsAndMaps() throws Exception {
		List<Integer> intList = Arrays.asList(1, 2, 3);
		List<String> stringList = Arrays.asList("1", "2", "3");
		Map<String, Object> m1 = Maps.makeMap("a", 1);
		Map<String, Object> m2 = Maps.makeMap("b", "2", "c", 3);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listOfMaps = Arrays.<Map<String, Object>> asList(m1, m2);

		String url = "/tests/" + getClass().getSimpleName();
		facard.delete(url, IResponseCallback.Utils.memoryCallback()).get();
		checkModifyAndGetData(url, "intList", intList, Maps.<String, Object> makeMap("intList", intList));
		checkModifyAndGetData(url, "stringList", stringList, Maps.<String, Object> makeMap("intList", intList, "stringList", stringList));
		checkModifyAndGetData(url, "mapList", listOfMaps, Maps.<String, Object> makeMap("intList", intList, "stringList", stringList, "mapList", listOfMaps));
	}

	@SuppressWarnings("unchecked")
	private void checkModifyAndGetJarData(String key, String value, Map<String, Object> expected) throws Exception {
		Map<String, Object> context1 = Maps.makeMap("c", 1);
		Map<String, Object> context2 = Maps.makeMap("c", 2);
		// Map<String, Object> entityDefn = Maps.makeMap();
		Map<String, Object> actionGet = Maps.makeMap(RepositoryConstants.entity, RepositoryConstants.entityJarData, RepositoryConstants.action, RepositoryConstants.actionGet);
		Map<String, Object> actionPost = Maps.makeMap(RepositoryConstants.entity, RepositoryConstants.entityJarData, RepositoryConstants.action, RepositoryConstants.actionPost);

		MemoryStatusChangedListener validListener = IStatusChangedListener.Utils.memory();
		repository.addStatusListener(validListener);
		repository.modifyJarData(digest, key, value, context1).get();

		validListener.assertEquals(//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37", REQUESTED, null, Maps.merge(context1, actionPost), //
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37", FOUND, expected, Maps.merge(context1, actionPost));
		repository.getJarData(digest, context2).get();
		validListener.assertEquals(//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37", REQUESTED, null, Maps.merge(context1, actionPost),//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37", FOUND, expected, Maps.merge(context1, actionPost),//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37", REQUESTED, null, Maps.merge(context2, actionGet),//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37", FOUND, expected, Maps.merge(context2, actionGet));
	}

	private void checkModifyAndGetData(String url, String key, Object value, Map<String, Object> expected) throws Exception {
		Map<String, Object> context1 = Maps.makeMap("c", 1);
		Map<String, Object> context2 = Maps.makeMap("c", 2);
		// Map<String, Object> entityDefn = Maps.makeMap();
		Map<String, Object> actionGet = Maps.makeMap(RepositoryConstants.action, RepositoryConstants.actionGet);
		Map<String, Object> actionPost = Maps.makeMap(RepositoryConstants.action, RepositoryConstants.actionPost);

		MemoryStatusChangedListener validListener = IStatusChangedListener.Utils.memory();
		repository.addStatusListener(validListener);
		repository.modifyData(url, key, value, context1).get();
		validListener.assertEquals(//
				url, REQUESTED, null, Maps.merge(context1, actionPost), //
				url, FOUND, expected, Maps.merge(context1, actionPost));
		repository.getData(url, context2).get();
		validListener.assertEquals(//
				url, REQUESTED, null, Maps.merge(context1, actionPost),//
				url, FOUND, expected, Maps.merge(context1, actionPost),//
				url, REQUESTED, null, Maps.merge(context2, actionGet),//
				url, FOUND, expected, Maps.merge(context2, actionGet));

	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		urlGenerator = new UrlGenerator();
		facard = IRepositoryFacard.Utils.defaultFacard();
		jarDigestor = IJarDigester.Utils.digester();
		repository = IArc4EclipseRepository.Utils.repository(facard, urlGenerator, jarDigestor);
		digest = "48292d38f6d060f873891171e1df689b3eaa0b37";
	}
}
