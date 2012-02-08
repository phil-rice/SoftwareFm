package org.softwareFm.softwareFmServer;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.IUser;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.server.GitTest;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.ProjectMock;
import org.softwareFm.server.ICrowdSourcedServer;

abstract public class GroupsTest extends GitTest {
	protected final IUrlGenerator userGenerator = LoginConstants.userGenerator();
	protected final IUrlGenerator groupGenerator = GroupConstants.groupsGenerator();

	protected final String groupCrypto = Crypto.makeKey();

	protected final String user1Crypto = Crypto.makeKey();
	protected final String user2Crypto = Crypto.makeKey();

	protected final String user1ProjectCrypto = Crypto.makeKey();
	protected final String user2ProjectCrypto = Crypto.makeKey();

	protected final String groupId = "groupId";

	protected final String id1 = "sfm1";
	protected final String id2 = "sfm2";

	protected void saveUserData(String softwaerFmId, String userCrypto, String projectCrypto) {
		IUser user = ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, userGenerator, Strings.firstNSegments(3));
		user.setUserProperty(softwaerFmId, userCrypto, SoftwareFmConstants.projectCryptoKey, projectCrypto);
	}

	protected void saveProjectData(String softwareFmId, String month, String projectCrypto, ProjectMock project) {
		String url = Urls.compose(userGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId)), SoftwareFmConstants.projectDirectoryName);
		File projectDirectory = new File(remoteRoot, url);
		Map<String, Map<String, List<Integer>>> projectDetails = project.getProjectDetails(softwareFmId, projectCrypto, month);
		File file = new File(projectDirectory, month);
		Files.makeDirectoryForFile(file);
		Files.setText(file, Crypto.aesEncrypt(projectCrypto, Json.toString(projectDetails)));
	}
}
