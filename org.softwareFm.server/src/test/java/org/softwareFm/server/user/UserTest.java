package org.softwareFm.server.user;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.internal.GitTest;
import org.softwareFm.server.internal.LocalGitClient;
import org.softwareFm.server.internal.User;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.tests.Tests;
import org.softwareFm.utilities.url.IUrlGenerator;
import org.softwareFm.utilities.url.UrlGenerator;
import org.softwareFm.utilities.url.Urls;

public class UserTest extends GitTest {

	private IUser user;
	private IUrlGenerator userGenerator;
	private IUrlGenerator projectDetailGenerator;
	private String key;
	private File repositoryDirectory;
	private File dotGit;

	private final String groupId1 = "groupId1";
	private final String artifactId11 = "artifactId11";
	private final String groupId2 = "groupId2";
	private final String artifactId21 = "artifactId21";
	private Map<String, Object> userDetails;
	private Map<String, Object> userAndProjectDetails;

	public void testSaveUserDetailsWhenRepositoryDoesntExist() {
		assertFalse(dotGit.exists());
		user.saveUserDetails(userDetails, v11);
		assertTrue(dotGit.exists());

		String url = userGenerator.findUrlFor(userDetails);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, ServerConstants.dataFileName, key);
		File file = fileDescription.getFile(localRoot);
		assertEquals(v11, fileDescription.decode(Files.getText(file)));
	}

	public void testSaveUserDetailsWhenRepositoryExists() {
		assertFalse(dotGit.exists());
		gitFacard.createRepository(dotGit, "");
		assertTrue(dotGit.exists());
		user.saveUserDetails(userDetails, v11);

		String url = userGenerator.findUrlFor(userDetails);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, ServerConstants.dataFileName, key);
		File file = fileDescription.getFile(localRoot);
		assertEquals(v11, fileDescription.decode(Files.getText(file)));
	}

	public void testGetUserDetails() {
		user.saveUserDetails(userDetails, v11);
		assertEquals(v11, user.getUserDetails(userDetails));
	}

	public void testAddToProjectWhenGroupAndArtifactIdExist() {
		Map<String, Object> initial = Maps.stringObjectMap(groupId1, Maps.stringObjectMap(artifactId11, Arrays.asList(1l)));
		Map<String, Object> initialCopy = Maps.copyMap(initial);

		Map<String, Object> expected = Maps.stringObjectMap(groupId1, Maps.stringObjectMap(artifactId11, Arrays.asList(1l, 3l)));

		assertEquals(expected, user.addProjectDetails(userAndProjectDetails, "month", 3, initial));
		assertEquals(initialCopy, initial);

		assertEquals(null, user.addProjectDetails(userAndProjectDetails, "month", 1, initial));
		assertEquals(null, user.addProjectDetails(userAndProjectDetails, "month", 3, expected));
	}

	public void testAddToProjectWhenGroupAndArtifactIdDontExist() {
		Map<String, Object> initial = Maps.stringObjectMap(groupId2, Maps.stringObjectMap(artifactId21, Arrays.asList(1l)));
		Map<String, Object> initialCopy = Maps.copyMap(initial);

		Map<String, Object> expected = Maps.stringObjectMap(//
				groupId1, Maps.stringObjectMap(artifactId11, Arrays.asList(1l)),//
				groupId2, Maps.stringObjectMap(artifactId21, Arrays.asList(1l)));

		assertEquals(expected, user.addProjectDetails(userAndProjectDetails, "month", 1, initial));
		assertEquals(initialCopy, initial);
	}

	public void testSaveAndGetProjectDetails() {
		user.saveProjectDetails(userDetails, "month", v11);
		assertEquals(v11, user.getProjectDetails(userDetails, "month"));

		String url = projectDetailGenerator.findUrlFor(userDetails);
		File file = new File(localRoot, Urls.compose(url, "month"));

		String text = Files.getText(file);
		String decrypted = Crypto.aesDecrypt(key, text);
		Map<String, Object> actual = Json.mapFromString(decrypted);
		assertEquals(v11, actual);
	}

	public void testGetUserDetailsWhenTHeyDontExist() {
		Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				user.getUserDetails(userDetails);
			}
		});
	}

	public void testGetProjectDetailsWhenTheyDontExist() {
		assertEquals(Maps.emptyStringObjectMap(), user.getProjectDetails(userAndProjectDetails, "month"));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		LocalGitClient client = new LocalGitClient(localRoot);
		userGenerator = new UrlGenerator("user/{0}/{1}/{2}", User.userKey);
		projectDetailGenerator = new UrlGenerator("user/{0}/{1}/{2}/projects", User.userKey);
		user = IUser.Utils.makeUserDetails(client, userGenerator, projectDetailGenerator, "g", "a");
		key = Crypto.makeKey();
		userDetails = Maps.stringObjectMap(User.userKey, "sfmUser", User.cryptoKey, key);
		userAndProjectDetails = Maps.with(userDetails, "g", groupId1, "a", artifactId11);

		repositoryDirectory = new File(localRoot, "user/sf/mU");
		dotGit = new File(repositoryDirectory, ServerConstants.DOT_GIT);
	}

}
