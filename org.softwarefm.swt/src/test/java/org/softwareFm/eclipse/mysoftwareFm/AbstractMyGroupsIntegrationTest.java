package org.softwareFm.eclipse.mysoftwareFm;


import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups.MyGroupsComposite;
import org.softwareFm.eclipse.mysoftwareFm.MyPeople.MyPeopleComposite;
import org.softwareFm.eclipse.usage.internal.AbstractExplorerIntegrationTest;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.explorer.internal.MySoftwareFmLoggedIn.MySoftwareFmLoggedInComposite;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;

abstract public class AbstractMyGroupsIntegrationTest extends AbstractExplorerIntegrationTest {

	protected final String softwareFmId0 = "someNewSoftwareFmId0";
	protected final String softwareFmId1 = "someNewSoftwareFmId1";
	protected final String softwareFmId2 = "someNewSoftwareFmId2";
	protected final String softwareFmId3 = "someNewSoftwareFmId3";

	protected final String email0 = softwareFmId0+"@someEmail.com";
	protected final String email1 = softwareFmId1+"@someEmail.com";
	protected final String email2 = softwareFmId2+"@someEmail.com";
	protected final String email3 = softwareFmId3+"@someEmail.com";
	protected final UserData userData = new UserData(email0, softwareFmId0, userKey0);

	protected MyGroupsComposite displayMySoftwareClickMyGroup() {
		userDataManager.setUserData(this, userData);
		explorer.showMySoftwareFm();
		Control mySoftwareFmComposite = masterDetailSocial.getMasterContent();
		MySoftwareFmLoggedInComposite composite = Swts.<MySoftwareFmLoggedInComposite> getDescendant(mySoftwareFmComposite, 0);
		Button myGroupsButton = composite.myGroupsButton;
		Swts.Buttons.press(myGroupsButton);
		MyGroupsComposite myGroups = dispatchUntilMyGroups();
		return myGroups;
	}

	protected MyGroupsComposite dispatchUntilMyGroups() {
		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control detail = masterDetailSocial.getDetailContent();
				return detail instanceof MyGroupsComposite;
			}
		});
		MyGroupsComposite myGroups = (MyGroupsComposite) masterDetailSocial.getDetailContent();
		if (myGroups == null)
			throw new NullPointerException();// is this because of a race condition... has intermittantly occured twice now
		return myGroups;
	}

	protected MyPeopleComposite displayMyPeople(final String groupId, final String artifactId, final int expectedTableCount) {
		userDataManager.setUserData(this, userData);
		explorer.showPeople(groupId, artifactId);
		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control detailContent = masterDetailSocial.getDetailContent();
				return detailContent instanceof MyPeopleComposite;
			}
		});
		dispatchUntil(new Callable<Boolean>() {
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
		dispatchUntil(new Callable<Boolean>() {
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
		truncateUsersTable();
		assertEquals(softwareFmId0, createUser());
	}


	protected void addUserToGroup(String softwareFmId, String email, String groupId, String groupCryptoKey, String status) {
		String crypto = getUserCryptoAccess().getCryptoForUser(softwareFmId);
		String usersProjectCryptoKey = IUserReader.Utils.getUserProperty(getServerApi().makeReader(), softwareFmId, crypto, JarAndPathConstants.projectCryptoKey);

		addUserToGroup(softwareFmId, crypto, usersProjectCryptoKey, groupId, groupCryptoKey, email, status);
	}

	protected void addUserToGroup(final String softwareFmId, final String crypto, String usersProjectCryptoKey, final String groupId, final String groupCryptoKey, String email, final String status) {
		assertNotNull(usersProjectCryptoKey);
		final Map<String, Object> initialData = Maps.stringObjectMap(LoginConstants.emailKey, email,//
				LoginConstants.softwareFmIdKey, softwareFmId, //
				JarAndPathConstants.projectCryptoKey, usersProjectCryptoKey, //
				GroupConstants.membershipStatusKey, status);
		getServerApi().makeReadWriter().modifyUserMembership(new ICallback2<IGroups, IUserMembership>(){
			@Override
			public void process(IGroups groups, IUserMembership membershipForServer) throws Exception {
				groups.addUser(groupId, groupCryptoKey, initialData);
				membershipForServer.addMembership(softwareFmId, crypto, groupId, groupCryptoKey, status);
			}});
	}


	protected void addUserToGroup1AndGroup2() {
		assertEquals(groupId0, createGroup(groupId0 +"Name", groupCryptoKey0));
		addUserToGroup(softwareFmId0, email0, groupId0, groupCryptoKey0, "someStatus1");

		assertEquals(groupId1, createGroup(groupId1 +"Name", groupCryptoKey1));
		addUserToGroup(softwareFmId0, email0, groupId1, groupCryptoKey1, "someStatus2");
	}

	protected Table getMyGroupsTable(MyGroupsComposite myGroupsComposite) {
		SashForm sashForm = myGroupsComposite.getEditor();
		assertEquals(3, sashForm.getChildren().length);
		assertTrue(sashForm.getChildren()[1] instanceof Composite);
		assertTrue(sashForm.getChildren()[2] instanceof Sash);

		Table table = (Table) sashForm.getChildren()[0];
		return table;
	}

	protected Table getMembershipTable(MyGroupsComposite myGroupsComposite) {
		SashForm sashForm = myGroupsComposite.getEditor();
		assertEquals(3, sashForm.getChildren().length);
		assertTrue(sashForm.getChildren()[1] instanceof Composite);
		assertTrue(sashForm.getChildren()[2] instanceof Sash);
		Composite rhsComposite = (Composite) sashForm.getChildren()[1];
		StackLayout stackLayout = (StackLayout) rhsComposite.getLayout();

		Control topControl = stackLayout.topControl;
		if (topControl instanceof Table)
			return (Table) topControl;
		else
			return null;
	}


}
