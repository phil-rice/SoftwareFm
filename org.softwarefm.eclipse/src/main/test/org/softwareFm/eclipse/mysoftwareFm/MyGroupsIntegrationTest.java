package org.softwareFm.eclipse.mysoftwareFm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.constants.CommonMessages;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups.MyGroupsComposite;
import org.softwareFm.swt.swt.Swts;

public class MyGroupsIntegrationTest extends AbstractMyGroupsIntegrationTest {

	public void testContentsWhenUserNotMemberOfGroups() {
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table table = getMyGroupsTable(myGroupsComposite);
		assertEquals(0, table.getItemCount());
		assertEquals(-1, table.getSelectionIndex());

	}

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

	protected void checkMainTable(Table table) {
		checkTableColumns(table, "Group Name", "Members", "My Status");
		checkTable(table, 0, new IdNameAndStatus("groupId1", "groupId1Name", "someStatus1"), "groupId1Name", "1", "someStatus1");
		checkTable(table, 1, new IdNameAndStatus("groupId2", "groupId2Name", "someStatus2"), "groupId2Name", "1", "someStatus2");
		assertEquals(2, table.getItemCount());
	}

	public void testClickingOnMyGroupsOpensSummaryTable() {
		addUserToGroup1AndGroup2();

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
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
		checkTableColumns(summaryTable, "Email", "Status");
		checkTable(summaryTable, 0, null, email2, "someStatus2");
		assertEquals(1, summaryTable.getItemCount());
	}

	public void testMyGroupsWhenOneLineIsBadlyEncrypted() {
		signUpUser(softwareFmId1, email1);
		signUpUser(softwareFmId2, email2);
		createGroup(groupId1, groupCryptoKey1);
		addUserToGroup(softwareFmId, email, groupId1, groupCryptoKey1, "someStatus1");
		addUserToGroup(softwareFmId1, email1, groupId1, groupCryptoKey2, "someStatus2"); // note bad crypto key
		addUserToGroup(softwareFmId2, email2, groupId1, groupCryptoKey1, "someStatus3");

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table table = getMyGroupsTable(myGroupsComposite);
		checkTableColumns(table, "Group Name", "Members", "My Status");
		checkTable(table, 0, new IdNameAndStatus("groupId1", "groupId1Name", "someStatus1"), "groupId1Name", "3", "someStatus1");
		assertEquals(1, table.getItemCount());

		table.select(0);
		table.notifyListeners(SWT.Selection, new Event());
		dispatchUntilQueueEmpty();

		checkTableColumns(table, "Group Name", "Members", "My Status");
		checkTable(table, 0, new IdNameAndStatus("groupId1", "groupId1Name", "someStatus1"), "groupId1Name", "3", "someStatus1");
		assertEquals(1, table.getItemCount());
		assertEquals(0, table.getSelectionIndex());

		StackLayout stackLayout = (StackLayout) Swts.<Composite> getDescendant(myGroupsComposite.getEditor(), 1).getLayout();
		Table summaryTable = Swts.getDescendant(myGroupsComposite.getEditor(), 1, 1);

		assertEquals(summaryTable, stackLayout.topControl);
		checkTableColumns(summaryTable, "Email", "Status");
		checkTable(summaryTable, 0, null, "Corrupted", "Record");
		checkTable(summaryTable, 1, null, email2, "someStatus3");
		checkTable(summaryTable, 2, null, email, "someStatus1");
		assertEquals(3, summaryTable.getItemCount());
	}

	public void testMyGroupsWhenMembershipTableBadlyEncrypted() {
		createGroup(groupId1, groupCryptoKey1);
		createGroup(groupId2, groupCryptoKey2);

		addUserToGroup(softwareFmId, email, groupId2, groupCryptoKey2, "someStatus2");

		String badCrypto = Crypto.makeKey();
		String userUrl = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(userUrl, GroupConstants.membershipFileName, badCrypto);
		processCallParameters.gitOperations.append(fileDescription, Maps.stringObjectMap("will be", "encoded wrong"));

		addUserToGroup(softwareFmId, email, groupId1, groupCryptoKey1, "someStatus1");

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table table = getMyGroupsTable(myGroupsComposite);
		checkTableColumns(table, "Group Name", "Members", "My Status");
		checkTable(table, 0, null, CommonMessages.corrupted, CommonMessages.record, "");
		checkTable(table, 1, new IdNameAndStatus("groupId1", "groupId1Name", "someStatus1"), "groupId1Name", "1", "someStatus1");
		checkTable(table, 2, new IdNameAndStatus("groupId2", "groupId2Name", "someStatus2"), "groupId2Name", "1", "someStatus2");
		assertEquals(3, table.getItemCount());
		assertEquals(-1, table.getSelectionIndex());

	}
}
