package org.arc4eclipse.arc4eclipseRepository.api.impl;

import static org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus.*;

import java.util.Map;

import junit.framework.TestCase;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.MemoryStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
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
				Arc4EclipseRepositoryConstants.hexDigestKey, digest, //
				"jcr:primaryType", "nt:unstructured",//
				"name1", "value1"));

		checkModifyAndGetJarData(Arc4EclipseRepositoryConstants.organisationUrlKey, "OrgUrl", Maps.<String, Object> makeMap(//
				Arc4EclipseRepositoryConstants.hexDigestKey, digest, //
				Arc4EclipseRepositoryConstants.organisationUrlKey, "OrgUrl",//
				"jcr:primaryType", "nt:unstructured",//
				"name1", "value1"));

		checkModifyAndGetJarData(Arc4EclipseRepositoryConstants.projectUrlKey, "ProjName", Maps.<String, Object> makeMap(//
				Arc4EclipseRepositoryConstants.hexDigestKey, digest, //
				Arc4EclipseRepositoryConstants.organisationUrlKey, "OrgUrl",//
				Arc4EclipseRepositoryConstants.projectUrlKey, "ProjName",//
				"jcr:primaryType", "nt:unstructured",//
				"name1", "value1"));
	}

	private void checkModifyAndGetJarData(String key, String value, Map<String, Object> expected) throws Exception {
		MemoryStatusChangedListener validListener = IStatusChangedListener.Utils.memory();
		repository.addStatusListener(validListener);
		repository.modifyJarData(digest, key, value).get();
		validListener.assertEquals(//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37", REQUESTED, null,//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37.json", FOUND, expected);
		repository.getJarData(digest).get();
		validListener.assertEquals(//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37", REQUESTED, null,//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37.json", FOUND, expected,//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37", REQUESTED, null,//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37.json", FOUND, expected);
	}

	@Test
	public void testGetAndModifyData() throws Exception {
		String url = "/tests/" + getClass().getSimpleName();
		facard.delete(url, IResponseCallback.Utils.memoryCallback()).get();
		checkModifyAndGetData(url, Arc4EclipseRepositoryConstants.organisationUrlKey, "orgUrl", Maps.<String, Object> makeMap(//
				Arc4EclipseRepositoryConstants.organisationUrlKey, "orgUrl",//
				"jcr:primaryType", "nt:unstructured"));
		checkModifyAndGetData(url, Arc4EclipseRepositoryConstants.organisationNameKey, "orgName", Maps.<String, Object> makeMap(//
				Arc4EclipseRepositoryConstants.organisationUrlKey, "orgUrl",//
				"jcr:primaryType", "nt:unstructured",//
				Arc4EclipseRepositoryConstants.organisationNameKey, "orgName"));
		checkModifyAndGetData(url, Arc4EclipseRepositoryConstants.descriptionKey, "orgDesc", Maps.<String, Object> makeMap(//
				Arc4EclipseRepositoryConstants.organisationUrlKey, "orgUrl",//
				Arc4EclipseRepositoryConstants.organisationNameKey, "orgName",//
				"jcr:primaryType", "nt:unstructured",//
				Arc4EclipseRepositoryConstants.descriptionKey, "orgDesc"));
	}

	private void checkModifyAndGetData(String url, String key, String value, Map<String, Object> expected) throws Exception {
		MemoryStatusChangedListener validListener = IStatusChangedListener.Utils.memory();
		repository.addStatusListener(validListener);
		repository.modifyData(url, key, value).get();
		validListener.assertEquals(//
				url, REQUESTED, null,//
				url + ".json", FOUND, expected);
		repository.getData(url).get();
		validListener.assertEquals(//
				url, REQUESTED, null,//
				url + ".json", FOUND, expected,//
				url, REQUESTED, null,//
				url + ".json", FOUND, expected);

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
