package org.arc4eclipse.arc4eclipseRepository.api.impl;

import static org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus.*;

import java.io.File;

import junit.framework.TestCase;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IJarDigester;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.MemoryStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.springframework.core.io.ClassPathResource;

public class Arc4EclipseRepositoryTest extends TestCase {

	private IArc4EclipseRepository repository;

	private File antFile;
	private File classWorldsFile;

	private UrlGenerator urlGenerator;

	private IRepositoryFacard facard;

	private IJarDigester jarDigestor;

	public void testSetup() {
		assertTrue(antFile.exists());
		assertTrue(classWorldsFile.exists());
	}

	public void testGetAndModifyJarData() throws Exception {
		facard.delete("/" + urlGenerator.forJar().apply(jarDigestor.apply(antFile)), IResponseCallback.Utils.memoryCallback());
		final String expectedDigest = "48292d38f6d060f873891171e1df689b3eaa0b37";
		checkModifyAndGetJarData("name1", "value1", IRepositoryDataItem.Utils.jarData(//
				Arc4EclipseRepositoryConstants.hexDigestKey, expectedDigest, //
				"jcr:primaryType", "nt:unstructured",//
				"name1", "value1"));

		checkModifyAndGetJarData(Arc4EclipseRepositoryConstants.organisationUrlKey, "OrgUrl", IRepositoryDataItem.Utils.jarData(//
				Arc4EclipseRepositoryConstants.hexDigestKey, expectedDigest, //
				Arc4EclipseRepositoryConstants.organisationUrlKey, "OrgUrl",//
				"jcr:primaryType", "nt:unstructured",//
				"name1", "value1"));

		checkModifyAndGetJarData(Arc4EclipseRepositoryConstants.projectUrlKey, "ProjName", IRepositoryDataItem.Utils.jarData(//
				Arc4EclipseRepositoryConstants.hexDigestKey, expectedDigest, //
				Arc4EclipseRepositoryConstants.organisationUrlKey, "OrgUrl",//
				Arc4EclipseRepositoryConstants.projectUrlKey, "ProjName",//
				"jcr:primaryType", "nt:unstructured",//
				"name1", "value1"));
	}

	private void checkModifyAndGetJarData(String key, String value, IJarData expected) {
		MemoryStatusChangedListener<IJarData> validListener = IStatusChangedListener.Utils.memory();
		MemoryStatusChangedListener<IOrganisationData> inValidListener = IStatusChangedListener.Utils.memory();
		repository.addStatusListener(IJarData.class, validListener);
		repository.addStatusListener(IOrganisationData.class, inValidListener);
		repository.modifyJarData(antFile, key, value);
		validListener.assertEquals(//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37", IJarData.class, REQUESTED, null,//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37.json", IJarData.class, FOUND, expected);
		repository.getJarData(antFile);
		validListener.assertEquals(//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37", IJarData.class, REQUESTED, null,//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37.json", IJarData.class, FOUND, expected,//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37", IJarData.class, REQUESTED, null,//
				"/jars/a48/a48292d38f6d060f873891171e1df689b3eaa0b37.json", IJarData.class, FOUND, expected);
		inValidListener.assertEquals();
	}

	public void testGetAndModifyData() {
		String url = "/tests/" + getClass().getSimpleName();
		facard.delete(url, IResponseCallback.Utils.memoryCallback());
		checkModifyAndGetData(url, Arc4EclipseRepositoryConstants.organisationUrlKey, "orgUrl", IOrganisationData.class, IRepositoryDataItem.Utils.organisationData(//
				Arc4EclipseRepositoryConstants.organisationUrlKey, "orgUrl",//
				"jcr:primaryType", "nt:unstructured"));
		checkModifyAndGetData(url, Arc4EclipseRepositoryConstants.organisationNameKey, "orgName", IOrganisationData.class, IRepositoryDataItem.Utils.organisationData(//
				Arc4EclipseRepositoryConstants.organisationUrlKey, "orgUrl",//
				"jcr:primaryType", "nt:unstructured",//
				Arc4EclipseRepositoryConstants.organisationNameKey, "orgName"));
		checkModifyAndGetData(url, Arc4EclipseRepositoryConstants.descriptionKey, "orgDesc", IOrganisationData.class, IRepositoryDataItem.Utils.organisationData(//
				Arc4EclipseRepositoryConstants.organisationUrlKey, "orgUrl",//
				Arc4EclipseRepositoryConstants.organisationNameKey, "orgName",//
				"jcr:primaryType", "nt:unstructured",//
				Arc4EclipseRepositoryConstants.descriptionKey, "orgDesc"));
	}

	private <T extends IRepositoryDataItem> void checkModifyAndGetData(String url, String key, String value, Class<T> clazz, T expected) {
		MemoryStatusChangedListener<T> validListener = IStatusChangedListener.Utils.memory();
		MemoryStatusChangedListener<IJarData> inValidListener = IStatusChangedListener.Utils.memory();
		repository.addStatusListener(clazz, validListener);
		repository.addStatusListener(IJarData.class, inValidListener);
		repository.modifyData(url, key, value, clazz);
		validListener.assertEquals(//
				url, clazz, REQUESTED, null,//
				url + ".json", clazz, FOUND, expected);
		repository.getData(url, clazz);
		validListener.assertEquals(//
				url, clazz, REQUESTED, null,//
				url + ".json", clazz, FOUND, expected,//
				url, clazz, REQUESTED, null,//
				url + ".json", clazz, FOUND, expected);
		inValidListener.assertEquals();

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		urlGenerator = new UrlGenerator();
		facard = IRepositoryFacard.Utils.defaultFacard();
		jarDigestor = new JarDigestor();
		repository = IArc4EclipseRepository.Utils.repository(facard, urlGenerator, jarDigestor);
		antFile = new ClassPathResource("ant-nodeps-1.6.5.jar").getFile();
		classWorldsFile = new ClassPathResource("classworlds-1.1.jar").getFile();
	}
}
