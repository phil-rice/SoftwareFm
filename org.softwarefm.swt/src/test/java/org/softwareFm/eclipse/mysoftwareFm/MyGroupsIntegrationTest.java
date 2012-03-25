package org.softwareFm.eclipse.mysoftwareFm;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.constants.CommonMessages;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups.MyGroupsComposite;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.swt.Swts;

public class MyGroupsIntegrationTest extends AbstractMyGroupsIntegrationTest {
	public void testContentsWhenMemberOfTwoGroups() {
		addUserToGroup1AndGroup2();

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table table = getMyGroupsTable(myGroupsComposite);
		checkMainTable(table);
		assertEquals(-1, table.getSelectionIndex());

		StackLayout stackLayout = (StackLayout) Swts.<Composite> getDescendant(myGroupsComposite.getEditor(), 1).getLayout();
		StyledText text = Swts.getDescendant(myGroupsComposite.getEditor(), 1, 0);
		assertEquals(text, stackLayout.topControl);
		assertEquals("Select a group to see members", text.getText());
	}

	public void testContentsWhenUserNotMemberOfGroups() {
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table table = getMyGroupsTable(myGroupsComposite);
		assertEquals(0, table.getItemCount());
		assertEquals(-1, table.getSelectionIndex());

	}

	protected void checkMainTable(Table table) {
		Swts.checkTableColumns(table, "Group Name", "Members", "My Status");
		Swts.checkTable(table, 0, new IdNameAndStatus(groupId0, groupName0, "someStatus1"), "groupId0Name", "1", "someStatus1");
		Swts.checkTable(table, 1, new IdNameAndStatus(groupId1, groupName1, "someStatus2"), "groupId1Name", "1", "someStatus2");
		assertEquals(2, table.getItemCount());
	}

	public void testClickingOnMyGroupsOpensSummaryTable() {
		addUserToGroup1AndGroup2();

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();// failed intermittantly once
		Table table = getMyGroupsTable(myGroupsComposite);
		checkMainTable(table);
		table.select(1);
		table.notifyListeners(SWT.Selection, new Event());
		dispatchUntilQueueEmpty();
		checkMainTable(table);
		assertEquals(1, table.getSelectionIndex());

		StackLayout stackLayout = (StackLayout) Swts.<Composite> getDescendant(myGroupsComposite.getEditor(), 1).getLayout();
		Table summaryTable = Swts.getDescendant(myGroupsComposite.getEditor(), 1, 1);

		assertEquals(summaryTable, stackLayout.topControl);
		Swts.checkTableColumns(summaryTable, "Email", "Status");
		Swts.checkTable(summaryTable, 0, makeMembershipMap(softwareFmId0, userKey0, email0, "someStatus2"), email0, "someStatus2");
		assertEquals(1, summaryTable.getItemCount());
	}

	public void testMyGroupsWhenOneLineIsBadlyEncrypted() {
		createUser(softwareFmId1, email1);
		createUser(softwareFmId2, email2);
		assertEquals(groupId0, createGroup(groupName0, groupCryptoKey0));
		addUserToGroup(softwareFmId0, email0, groupId0, groupCryptoKey0, "someStatus1");
		addUserToGroup(softwareFmId1, email1, groupId0, groupCryptoKey1, "someStatus2"); // note bad crypto key
		addUserToGroup(softwareFmId2, email2, groupId0, groupCryptoKey0, "someStatus3");

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table table = getMyGroupsTable(myGroupsComposite);
		Swts.checkTableColumns(table, "Group Name", "Members", "My Status");
		Swts.checkTable(table, 0, new IdNameAndStatus(groupId0, groupName0, "someStatus1"), groupName0, "3", "someStatus1");
		assertEquals(1, table.getItemCount());

		table.select(0);
		table.notifyListeners(SWT.Selection, new Event());
		dispatchUntilQueueEmpty();

		Swts.checkTableColumns(table, "Group Name", "Members", "My Status");
		Swts.checkTable(table, 0, new IdNameAndStatus(groupId0, groupName0, "someStatus1"), groupName0, "3", "someStatus1");
		assertEquals(1, table.getItemCount());
		assertEquals(0, table.getSelectionIndex());

		StackLayout stackLayout = (StackLayout) Swts.<Composite> getDescendant(myGroupsComposite.getEditor(), 1).getLayout();
		Table summaryTable = Swts.getDescendant(myGroupsComposite.getEditor(), 1, 1);

		assertEquals(summaryTable, stackLayout.topControl);
		Swts.checkTableColumns(summaryTable, "Email", "Status");
		Swts.checkTable(summaryTable, 0, null, "Corrupted", "Record");
		Swts.checkTable(summaryTable, 1, makeMembershipMap(softwareFmId0, userKey0, email0, "someStatus1"), email0, "someStatus1");
		Swts.checkTable(summaryTable, 2, makeMembershipMap(softwareFmId2, userKey2, email2, "someStatus3"), email2, "someStatus3");
		assertEquals(3, summaryTable.getItemCount());
	}

	public void testMyGroupsWhenMembershipTableBadlyEncrypted() {
		assertEquals(groupId0, createGroup(groupName0, groupCryptoKey0));
		assertEquals(groupId1, createGroup(groupName1, groupCryptoKey1));

		addUserToGroup(softwareFmId0, email0, groupId1, groupCryptoKey1, "someStatus2");

		String badCrypto = Crypto.makeKey();
		String userUrl = getServerConfig().userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0));
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(userUrl, GroupConstants.membershipFileName, badCrypto);
		getServerApi().makeContainer().gitOperations().append(fileDescription, Maps.stringObjectMap("will be", "encoded wrong"));

		addUserToGroup(softwareFmId0, email0, groupId0, groupCryptoKey0, "someStatus1");

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table table = getMyGroupsTable(myGroupsComposite);
		Swts.checkTableColumns(table, "Group Name", "Members", "My Status");
		Swts.checkTable(table, 0, null, CommonMessages.corrupted, CommonMessages.record, "");
		Swts.checkTable(table, 1, new IdNameAndStatus(groupId0, groupName0, "someStatus1"), groupName0, "1", "someStatus1");
		Swts.checkTable(table, 2, new IdNameAndStatus(groupId1, groupName1, "someStatus2"), groupName1, "1", "someStatus2");
		assertEquals(3, table.getItemCount());
		assertEquals(-1, table.getSelectionIndex());

		table.select(0); // this is the bad crypto
		Table summaryTable = Swts.getDescendant(myGroupsComposite.getEditor(), 1, 1);
		table.notifyListeners(SWT.Selection, new Event());
		dispatchUntilQueueEmpty();
		assertEquals(0, summaryTable.getItemCount());

		table.select(1);
		table.notifyListeners(SWT.Selection, new Event());
		dispatchUntilQueueEmpty();
		assertEquals(1, summaryTable.getItemCount());
		Swts.checkTable(summaryTable, 0, makeMembershipMap(softwareFmId0, userKey0, email0, "someStatus1"), email0, "someStatus1");

		table.select(0);
		table.notifyListeners(SWT.Selection, new Event());
		dispatchUntilQueueEmpty();
		assertEquals(0, summaryTable.getItemCount());

	}

	private Map<String, Object> makeMembershipMap(String softwareFmId, String cryptoKey, String email, String status) {
		String projectCryptoKey = IUserReader.Utils.getUserProperty(getServerApi().makeContainer(), softwareFmId, cryptoKey, JarAndPathConstants.projectCryptoKey);
		return Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, JarAndPathConstants.projectCryptoKey, projectCryptoKey, LoginConstants.emailKey, email, GroupConstants.membershipStatusKey, status);
	}
}
