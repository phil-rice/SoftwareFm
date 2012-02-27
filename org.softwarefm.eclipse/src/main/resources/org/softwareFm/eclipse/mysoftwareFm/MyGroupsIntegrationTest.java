package org.softwareFm.eclipse.mysoftwareFm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups.MyGroupsComposite;
import org.softwareFm.swt.swt.Swts;

public class MyGroupsIntegrationTest extends AbstractMyGroupsIntegrationTest {
	private final String groupId1 = "groupId1";
	private final String groupId2 = "groupId2";

	private final String groupCryptoKey1 = Crypto.makeKey();
	private final String groupCryptoKey2 = Crypto.makeKey();

	public void testContentsWhenUserNotMemberOfGroups() {
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table table = getMyGroupsTable(myGroupsComposite);
		System.out.println(table);
		assertEquals(0, table.getItemCount());
	}

	public void testContentsWhenMemberOfTwoGroups() {
		addUserToGroup1AndGroup2();

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table table = getMyGroupsTable(myGroupsComposite);
		System.out.println(table);
		checkMainTable(table);

		StackLayout stackLayout = (StackLayout) Swts.<Composite> getDescendant(myGroupsComposite.getEditor(), 1).getLayout();
		StyledText text = Swts.getDescendant(myGroupsComposite.getEditor(), 1, 0);
		assertEquals(text, stackLayout.topControl);
		assertEquals("Select a group to see members", text.getText());
	}

	protected void checkMainTable(Table table) {
		checkTableColumns(table, "Group Name", "Members", "My Status");
		checkTable(table, 0, "groupId1", "groupId1Name", "1", "someStatus1");
		checkTable(table, 1, "groupId2", "groupId2Name", "1", "someStatus2");
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

		StackLayout stackLayout = (StackLayout) Swts.<Composite> getDescendant(myGroupsComposite.getEditor(), 1).getLayout();
		Table summaryTable = Swts.getDescendant(myGroupsComposite.getEditor(), 1, 1);

		assertEquals(summaryTable, stackLayout.topControl);
		checkTableColumns(summaryTable, "Email", "Status");
		checkTable(summaryTable, 0, null, email, "someStatus2");
		assertEquals(1, summaryTable.getItemCount());

	}

	protected void addUserToGroup1AndGroup2() {
		createGroup(groupId1, groupCryptoKey1);
		addUserToGroup(groupId1, groupCryptoKey1, "someStatus1");

		createGroup(groupId2, groupCryptoKey2);
		addUserToGroup(groupId2, groupCryptoKey2, "someStatus2");
	}

	protected Table getMyGroupsTable(MyGroupsComposite myGroupsComposite) {
		SashForm sashForm = myGroupsComposite.getEditor();
		assertEquals(3, sashForm.getChildren().length);
		assertTrue(sashForm.getChildren()[1] instanceof Composite);
		assertTrue(sashForm.getChildren()[2] instanceof Sash);

		Table table = (Table) sashForm.getChildren()[0];
		return table;
	}
}
