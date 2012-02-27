package org.softwareFm.eclipse.mysoftwareFm;

import java.util.Map;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups.MyGroupsComposite;
import org.softwareFm.eclipse.usage.internal.AbstractExplorerIntegrationTest;
import org.softwareFm.softwareFmServer.GroupsForServer;
import org.softwareFm.softwareFmServer.UserMembershipForServer;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.explorer.internal.MySoftwareFmLoggedIn.MySoftwareFmLoggedInComposite;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;

abstract public class AbstractMyGroupsIntegrationTest extends AbstractExplorerIntegrationTest {

	protected final String email = "my@email.com";
	protected final String softwareFmId = "newSoftwareFmId0";
	protected final UserData userData = new UserData(email, softwareFmId, userCryptoKey);
	protected GroupsForServer groups;
	protected UserMembershipForServer membershipForServer;

	protected MyGroupsComposite displayMySoftwareClickMyGroup() {
		userDataManager.setUserData(this, userData);
		explorer.showMySoftwareFm();
		Control mySoftwareFmComposite = masterDetailSocial.getMasterContent();
		MySoftwareFmLoggedInComposite composite = Swts.<MySoftwareFmLoggedInComposite> getDescendant(mySoftwareFmComposite, 0);
		Button myGroupsButton = composite.myGroupsButton;
		Swts.Buttons.press(myGroupsButton);
		dispatchUntilQueueEmpty();
		MyGroupsComposite myGroups = (MyGroupsComposite) masterDetailSocial.getDetailContent();
		return myGroups;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IGitOperations remoteOperations = processCallParameters.gitOperations;
		IFunction1<String, String> repoDefnFn = Strings.firstNSegments(3);
		groups = new GroupsForServer(groupUrlGenerator, remoteOperations, repoDefnFn);
		user.setUserProperty(softwareFmId, userCryptoKey, LoginConstants.emailKey, email); // create a user
		membershipForServer = new UserMembershipForServer(userUrlGenerator, user, remoteOperations, repoDefnFn);

	}

	protected void createGroup(String groupId, String groupCryptoKey) {
		groups.setGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey, groupId + "Name");
		processCallParameters.aboveRepostoryUrlCache.clear();
	}

	protected void addUserToGroup(String groupId, String groupCryptoKey, String status) {
		String usersProjectCryptoKey = user.getUserProperty(softwareFmId, userCryptoKey, SoftwareFmConstants.projectCryptoKey);
		assertNotNull(usersProjectCryptoKey);
		Map<String, Object> initialData = Maps.stringObjectMap(LoginConstants.emailKey, email,//
				LoginConstants.softwareFmIdKey, softwareFmId, //
				SoftwareFmConstants.projectCryptoKey, usersProjectCryptoKey, //
				GroupConstants.membershipStatusKey, status);
		groups.addUser(groupId, groupCryptoKey, initialData);
		membershipForServer.addMembership(softwareFmId, userCryptoKey, groupId, groupCryptoKey, status);
	}

	@Override
	protected IUserReader makeUserReader() {
		IUrlGenerator userUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.userUrlKey);
		return IUserReader.Utils.localUserReader(gitLocal, userUrlGenerator);
	}
}
