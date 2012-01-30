package org.softwareFm.server.user;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.softwareFm.server.GitTest;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IUser;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.internal.LocalGitClient;
import org.softwareFm.server.internal.User;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.tests.Tests;
import org.softwareFm.utilities.url.IUrlGenerator;
import org.softwareFm.utilities.url.UrlGenerator;
import org.softwareFm.utilities.url.Urls;

public class UserAndProjectTest extends GitTest {

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
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, key);
		File file = fileDescription.getFile(localRoot);
		assertEquals(v11, fileDescription.decode(Files.getText(file)));
	}

	public void testSaveUserDetailsWhenRepositoryExists() {
		assertFalse(dotGit.exists());
		gitReader.createRepository(dotGit, "");
		assertTrue(dotGit.exists());
		user.saveUserDetails(userDetails, v11);

		String url = userGenerator.findUrlFor(userDetails);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, key);
		File file = fileDescription.getFile(localRoot);
		assertEquals(v11, fileDescription.decode(Files.getText(file)));
	}

	public void testGetUserDetails() {
		user.saveUserDetails(userDetails, v11);
		assertEquals(v11, user.getUserDetails(userDetails));
	}

	public void testGetProjectDetailsCreatesCryptoKeyInUserDetailsFirstTimeAndUsesItLater() {
		user.saveUserDetails(userDetails, v11);
		Map<String, Object> initialDetails = user.getUserDetails(userDetails);
		assertFalse(initialDetails.containsKey(LoginConstants.projectCryptoKey));

		user.getProjectDetails(userAndProjectDetails, "month");

		Map<String, Object> finalDetails = user.getUserDetails(userDetails);
		Object projectCrypto = finalDetails.get(LoginConstants.projectCryptoKey);

		Map<String, Object> finalDetails2 = user.getUserDetails(userDetails);
		Object projectCrypto2 = finalDetails2.get(LoginConstants.projectCryptoKey);

		assertEquals(projectCrypto, projectCrypto2);
		assertNotNull(projectCrypto);
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
		user.saveUserDetails(userDetails, v11); // need user details for project crypto key
		user.saveProjectDetails(userDetails, "month", v11);
		assertEquals(v11, user.getProjectDetails(userDetails, "month"));

		String url = projectDetailGenerator.findUrlFor(userDetails);
		File file = new File(localRoot, Urls.compose(url, "month"));

		String text = Files.getText(file);
		String projectCrypto = (String) user.getUserDetails(userDetails).get(LoginConstants.projectCryptoKey);
		
		String decrypted = Crypto.aesDecrypt(projectCrypto, text);
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

	public void testGetProjectDetailsWhenTheyDontExistAndUserDoesntExist() {
		Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				user.getProjectDetails(userAndProjectDetails, "month");
			}
		});
	}

	public void testGetProjectDetailsWhenTheyDontExist() {
		user.saveUserDetails(userDetails, v11); // need user details for project crypto key
		assertEquals(Maps.emptyStringObjectMap(), user.getProjectDetails(userAndProjectDetails, "month"));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		LocalGitClient client = new LocalGitClient(localRoot);
		userGenerator = new UrlGenerator("user/{0}/{1}/{2}", User.userKey);
		projectDetailGenerator = new UrlGenerator("user/{0}/{1}/{2}/projects", User.userKey);
		key = Crypto.makeKey();
		IFunction1<Map<String, Object>, String> cryptoFn = Functions.constant(key);
		user = IUser.Utils.makeUserDetails(client, userGenerator, projectDetailGenerator, cryptoFn, "g", "a");
		userDetails = Maps.stringObjectMap(User.userKey, "sfmUser");
		userAndProjectDetails = Maps.with(userDetails, "g", groupId1, "a", artifactId11);

		repositoryDirectory = new File(localRoot, "user/sf/mU");
		dotGit = new File(repositoryDirectory, CommonConstants.DOT_GIT);
	}

}
