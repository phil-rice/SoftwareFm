package org.arc4eclipse.arc4eclipseRepository.api.impl;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseCallback;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IJarDigester;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.IResponse;
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
		facard.delete("/" + urlGenerator.forJar(jarDigestor.apply(antFile)), IResponseCallback.Utils.memoryCallback());
		final String expectedDigest = "48292d38f6d060f873891171e1df689b3eaa0b37";
		final AtomicInteger count = new AtomicInteger();
		checkGetAndModifyJarData(antFile, "name1", "value1", new IArc4EclipseCallback<IJarData>() {
			@Override
			public void process(IResponse response, IJarData data) {
				assertNotNull(response.toString(), data);
				assertEquals("value1", data.get("name1"));
				assertEquals(response.toString(), expectedDigest, data.getHexDigest());
				assertEquals(response.toString(), "", data.getOrganisationUrl());
				assertEquals(response.toString(), "", data.getProjectName());
				assertEquals(response.toString(), "", data.getReleaseIdentifier());
				count.incrementAndGet();
			}
		});
		checkGetAndModifyJarData(antFile, Arc4EclipseRepositoryConstants.organisationUrlKey, "OrgUrl", new IArc4EclipseCallback<IJarData>() {
			@Override
			public void process(IResponse response, IJarData data) {
				assertNotNull(response.toString(), data);
				assertEquals("value1", data.get("name1"));
				assertEquals(response.toString(), expectedDigest, data.getHexDigest());
				assertEquals(response.toString(), "OrgUrl", data.getOrganisationUrl());
				assertEquals(response.toString(), "", data.getProjectName());
				assertEquals(response.toString(), "", data.getReleaseIdentifier());
				count.incrementAndGet();
			}
		});
		checkGetAndModifyJarData(antFile, Arc4EclipseRepositoryConstants.projectNameKey, "ProjName", new IArc4EclipseCallback<IJarData>() {
			@Override
			public void process(IResponse response, IJarData data) {
				assertNotNull(response.toString(), data);
				assertEquals("value1", data.get("name1"));
				assertEquals(response.toString(), expectedDigest, data.getHexDigest());
				assertEquals(response.toString(), "OrgUrl", data.getOrganisationUrl());
				assertEquals(response.toString(), "ProjName", data.getProjectName());
				assertEquals(response.toString(), "", data.getReleaseIdentifier());
				count.incrementAndGet();
			}
		});
		checkGetAndModifyJarData(antFile, Arc4EclipseRepositoryConstants.releaseIdentifierKey, "rel", new IArc4EclipseCallback<IJarData>() {
			@Override
			public void process(IResponse response, IJarData data) {
				assertNotNull(response.toString(), data);
				assertEquals("value1", data.get("name1"));
				assertEquals(response.toString(), expectedDigest, data.getHexDigest());
				assertEquals(response.toString(), "OrgUrl", data.getOrganisationUrl());
				assertEquals(response.toString(), "ProjName", data.getProjectName());
				assertEquals(response.toString(), "rel", data.getReleaseIdentifier());
				count.incrementAndGet();
			}
		});

		assertEquals(8, count.get());
	}

	public void testGetAndModifyData() {
		String url = "/" + getClass().getSimpleName();
		facard.delete(url, IResponseCallback.Utils.memoryCallback());
		final AtomicInteger count = new AtomicInteger();
		checkGetAndModifyData(url, Arc4EclipseRepositoryConstants.organisationUrlKey, "orgUrl", new IArc4EclipseCallback<IOrganisationData>() {
			@Override
			public void process(IResponse response, IOrganisationData data) {
				assertEquals("orgUrl", data.getOrganisationUrl());
				assertEquals("", data.getOrganisationName());
				assertEquals("", data.getDescription());
				count.incrementAndGet();
			}
		});
		checkGetAndModifyData(url, Arc4EclipseRepositoryConstants.organisationNameKey, "orgName", new IArc4EclipseCallback<IOrganisationData>() {
			@Override
			public void process(IResponse response, IOrganisationData data) {
				assertEquals("orgUrl", data.getOrganisationUrl());
				assertEquals("orgName", data.getOrganisationName());
				assertEquals("", data.getDescription());
				count.incrementAndGet();
			}
		});
		checkGetAndModifyData(url, Arc4EclipseRepositoryConstants.descriptionKey, "orgDesc", new IArc4EclipseCallback<IOrganisationData>() {
			@Override
			public void process(IResponse response, IOrganisationData data) {
				assertEquals("orgUrl", data.getOrganisationUrl());
				assertEquals("orgName", data.getOrganisationName());
				assertEquals("orgDesc", data.getDescription());
				count.incrementAndGet();
			}
		});
		assertEquals(6, count.get());
	}

	private void checkGetAndModifyData(String url, final String name, final String value, IArc4EclipseCallback<IOrganisationData> callback) {
		repository.modifyData(url, name, value, IArc4EclipseRepository.Utils.organisationData(), callback);
		repository.getData(url, IArc4EclipseRepository.Utils.organisationData(), callback);
		repository.getData(url, IArc4EclipseRepository.Utils.organisationData(), new IArc4EclipseCallback<IOrganisationData>() {
			@Override
			public void process(IResponse response, IOrganisationData data) {
				assertEquals(value, data.get(name));
			}
		});
	}

	private void checkGetAndModifyJarData(File jar, final String name, final String value, IArc4EclipseCallback<IJarData> callback) {
		repository.modifyJarData(jar, name, value, callback);
		repository.getJarData(jar, callback);
		repository.getJarData(jar, new IArc4EclipseCallback<IJarData>() {
			@Override
			public void process(IResponse response, IJarData data) {
				assertEquals(value, data.get(name));
			}
		});
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
