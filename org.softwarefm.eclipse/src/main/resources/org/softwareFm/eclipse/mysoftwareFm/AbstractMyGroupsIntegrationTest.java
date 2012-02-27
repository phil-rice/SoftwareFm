package org.softwareFm.eclipse.mysoftwareFm;

import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups.MyGroupsComposite;
import org.softwareFm.eclipse.mysoftwareFm.MyPeople.MyPeopleComposite;
import org.softwareFm.eclipse.usage.internal.AbstractExplorerIntegrationTest;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.explorer.internal.MySoftwareFmLoggedIn.MySoftwareFmLoggedInComposite;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;
import org.springframework.jdbc.core.JdbcTemplate;

abstract public class AbstractMyGroupsIntegrationTest extends AbstractExplorerIntegrationTest {
	protected final String groupId1 = "groupId1";
	protected final String groupId2 = "groupId2";

	protected final String groupCryptoKey1 = Crypto.makeKey();
	protected final String groupCryptoKey2 = Crypto.makeKey();

	protected final String email = "my@email.com";
	protected final String email1 = "my1@email.com";
	protected final String email2 = "my2@email.com";
	protected final String softwareFmId = "newSoftwareFmId0";
	protected final String softwareFmId1 = "newSoftwareFmId1";
	protected final String softwareFmId2 = "newSoftwareFmId2";
	protected final UserData userData = new UserData(email, softwareFmId, userCryptoKey);

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

	protected MyPeopleComposite displayMyPeople(final String groupId, final String artifactId, final int expectedTableCount) {
		userDataManager.setUserData(this, userData);
		explorer.showPeople(groupId, artifactId);
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control detailContent = masterDetailSocial.getDetailContent();
				return detailContent instanceof MyPeopleComposite;
			}
		});
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				MyPeopleComposite myPeopleComposite = (MyPeopleComposite) masterDetailSocial.getDetailContent();
				String text = myPeopleComposite.getTitle().getText();
				if (text.contains("Group Id: " + groupId + "  Artifact Id: " + artifactId))
					return true;
				assertEquals("Searching...", text);
				return false;
			}
		});
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				MyPeopleComposite myPeopleComposite = (MyPeopleComposite) masterDetailSocial.getDetailContent();
				Table table = myPeopleComposite.getEditor();
				int itemCount = table.getItemCount();
				return itemCount == expectedTableCount;
			}
		});
		return (MyPeopleComposite) masterDetailSocial.getDetailContent();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		new JdbcTemplate(processCallParameters.dataSource).update("delete from users");
		assertEquals(userCryptoKey, signUpUser(softwareFmId, email));

	}

	protected String signUpUser(String softwareFmId, String email) {
		SignUpResult signUp = processCallParameters.signUpChecker.signUp(email, softwareFmId + "Moniker", "someSalt", "somePassword", softwareFmId);
		if (signUp.errorMessage != null)
			fail(signUp.errorMessage);
		return signUp.crypto;
	}

	protected void createGroup(String groupId, String groupCryptoKey) {
		groups.setGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey, groupId + "Name");
		processCallParameters.aboveRepostoryUrlCache.clear();
	}

	protected void addUserToGroup(String softwareFmId, String email, String groupId, String groupCryptoKey, String status) {
		String crypto = Functions.call(processCallParameters.userCryptoFn, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));

		String usersProjectCryptoKey = user.getUserProperty(softwareFmId, crypto, SoftwareFmConstants.projectCryptoKey);
		assertNotNull(usersProjectCryptoKey);
		Map<String, Object> initialData = Maps.stringObjectMap(LoginConstants.emailKey, email,//
				LoginConstants.softwareFmIdKey, softwareFmId, //
				SoftwareFmConstants.projectCryptoKey, usersProjectCryptoKey, //
				GroupConstants.membershipStatusKey, status);
		groups.addUser(groupId, groupCryptoKey, initialData);
		membershipForServer.addMembership(softwareFmId, crypto, groupId, groupCryptoKey, status);
	}

	@Override
	protected IUserReader makeUserReader() {
		IUrlGenerator userUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.userUrlKey);
		return IUserReader.Utils.localUserReader(gitLocal, userUrlGenerator);
	}

	protected void addUserToGroup1AndGroup2() {
		createGroup(groupId1, groupCryptoKey1);
		addUserToGroup(softwareFmId, email1, groupId1, groupCryptoKey1, "someStatus1");

		createGroup(groupId2, groupCryptoKey2);
		addUserToGroup(softwareFmId, email2, groupId2, groupCryptoKey2, "someStatus2");
	}
}
