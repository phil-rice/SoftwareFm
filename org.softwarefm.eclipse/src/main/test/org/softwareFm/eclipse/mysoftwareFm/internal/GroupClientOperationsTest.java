package org.softwareFm.eclipse.mysoftwareFm.internal;

import java.util.Arrays;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.mysoftwareFm.AbstractMyGroupsIntegrationTest;
import org.softwareFm.eclipse.mysoftwareFm.IdNameAndStatus;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups.MyGroupsButtons;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups.MyGroupsComposite;
import org.softwareFm.swt.editors.NameAndValuesEditor.NameAndValuesEditorComposite;
import org.softwareFm.swt.swt.Swts;

public class GroupClientOperationsTest extends AbstractMyGroupsIntegrationTest {
	@SuppressWarnings("unchecked")
	public void testInvite() {
		createGroup(groupId1, groupCryptoKey1);
		addUserToGroup(softwareFmId, email, groupId1, groupCryptoKey1, GroupConstants.adminStatus);
		String groupName = groupId1 + "Name";

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table beforeTable = getMyGroupsTable(myGroupsComposite);
		beforeTable.select(0);
		beforeTable.notifyListeners(SWT.Selection, new Event());
		dispatchUntilQueueEmpty();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		assertTrue(buttons.invite.isEnabled());
		Swts.Buttons.press(buttons.invite);
		dispatchUntilQueueEmpty();

		NameAndValuesEditorComposite editor = (NameAndValuesEditorComposite) masterDetailSocial.getDetailContent();
		assertFalse(((Text) editor.values.getChildren()[0]).getEditable());
		checkChange(editor, 1, email1 + "," + email2);
		checkChange(editor, 2, "newSubject $email$/$group$");
		checkChange(editor, 3, "newMail $email$/$group$");
		assertTrue(editor.getFooter().okButton().isEnabled());

		editor.getFooter().ok();
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control detailContent = masterDetailSocial.getDetailContent();
				return detailContent instanceof MyGroupsComposite;
			}
		});

		Table afterTable = getMyGroupsTable((MyGroupsComposite) masterDetailSocial.getDetailContent());
		checkTable(afterTable, 0, new IdNameAndStatus(groupId1, groupName, GroupConstants.adminStatus), groupName, "3", GroupConstants.adminStatus);
		assertEquals(1, afterTable.getItemCount());
		assertEquals(0, afterTable.getSelectionIndex());

		assertEquals(Lists.times(2, email), mailer.froms);
		assertEquals(Arrays.asList(email1, email2), mailer.tos);
		assertEquals(Arrays.asList("newSubject " + email1 + "/" + groupName, "newSubject " + email2 + "/" + groupName), mailer.subjects);
		assertEquals(Arrays.asList("newMail " + email1 + "/" + groupName, "newMail " + email2 + "/" + groupName), mailer.messages);

		assertEquals(groupName, groups.getGroupProperty(groupId1, groupCryptoKey1, GroupConstants.groupNameKey));

		assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCryptoKey1, GroupConstants.membershipStatusKey, GroupConstants.adminStatus)), userMembershipReader.walkGroupsFor(softwareFmId, userCryptoKey));
		assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCryptoKey1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus)), userMembershipReader.walkGroupsFor(softwareFmId1, userCryptoKey1));
		assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCryptoKey1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus)), userMembershipReader.walkGroupsFor(softwareFmId2, userCryptoKey2));

		String projectCryptoKey = userReader.getUserProperty(softwareFmId, userCryptoKey, SoftwareFmConstants.projectCryptoKey);
		String projectCryptoKey1 = userReader.getUserProperty(softwareFmId1, userCryptoKey1, SoftwareFmConstants.projectCryptoKey);
		String projectCryptoKey2 = userReader.getUserProperty(softwareFmId2, userCryptoKey2, SoftwareFmConstants.projectCryptoKey);

		assertNotNull(projectCryptoKey);
		assertNotNull(projectCryptoKey1);
		assertNotNull(projectCryptoKey2);

		assertEquals(Arrays.asList(//
				Maps.stringObjectMap(SoftwareFmConstants.projectCryptoKey, projectCryptoKey, LoginConstants.emailKey, email, GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.softwareFmIdKey, softwareFmId), //
				Maps.stringObjectMap(SoftwareFmConstants.projectCryptoKey, projectCryptoKey1, LoginConstants.emailKey, email1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus, LoginConstants.softwareFmIdKey, softwareFmId1), //
				Maps.stringObjectMap(SoftwareFmConstants.projectCryptoKey, projectCryptoKey2, LoginConstants.emailKey, email2, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus, LoginConstants.softwareFmIdKey, softwareFmId2)), //
				Iterables.list(groupsReader.users(groupId1, groupCryptoKey1)));
	}

	@SuppressWarnings("unchecked")
	public void testDisplaysCreateActuallyCreatesSendsEmailsAndDisplaysMyGroupsAtEnd() {
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		Swts.Buttons.press(buttons.create);
		dispatchUntilQueueEmpty();
		NameAndValuesEditorComposite editor = (NameAndValuesEditorComposite) masterDetailSocial.getDetailContent();
		checkChange(editor, 0, "someNewGroupName");
		checkChange(editor, 1, email1 + "," + email2);
		checkChange(editor, 2, "newSubject $email$/$group$");
		checkChange(editor, 3, "newMail $email$/$group$");
		assertTrue(editor.getFooter().okButton().isEnabled());

		editor.getFooter().ok();
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control detailContent = masterDetailSocial.getDetailContent();
				return detailContent instanceof MyGroupsComposite;
			}
		});

		Table table = getMyGroupsTable((MyGroupsComposite) masterDetailSocial.getDetailContent());
		checkTable(table, 0, new IdNameAndStatus(groupId1, "someNewGroupName", GroupConstants.adminStatus), "someNewGroupName", "3", GroupConstants.adminStatus);
		assertEquals(1, table.getItemCount());
		assertEquals(0, table.getSelectionIndex());

		assertEquals(Lists.times(2, email), mailer.froms);
		assertEquals(Arrays.asList(email1, email2), mailer.tos);
		assertEquals(Arrays.asList("newSubject " + email1 + "/someNewGroupName", "newSubject " + email2 + "/someNewGroupName"), mailer.subjects);
		assertEquals(Arrays.asList("newMail " + email1 + "/someNewGroupName", "newMail " + email2 + "/someNewGroupName"), mailer.messages);

		assertEquals("someNewGroupName", groups.getGroupProperty(groupId1, groupCryptoKey1, GroupConstants.groupNameKey));

		assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCryptoKey1, GroupConstants.membershipStatusKey, GroupConstants.adminStatus)), userMembershipReader.walkGroupsFor(softwareFmId, userCryptoKey));
		assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCryptoKey1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus)), userMembershipReader.walkGroupsFor(softwareFmId1, userCryptoKey1));
		assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCryptoKey1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus)), userMembershipReader.walkGroupsFor(softwareFmId2, userCryptoKey2));

		String projectCryptoKey = userReader.getUserProperty(softwareFmId, userCryptoKey, SoftwareFmConstants.projectCryptoKey);
		String projectCryptoKey1 = userReader.getUserProperty(softwareFmId1, userCryptoKey1, SoftwareFmConstants.projectCryptoKey);
		String projectCryptoKey2 = userReader.getUserProperty(softwareFmId2, userCryptoKey2, SoftwareFmConstants.projectCryptoKey);

		assertNotNull(projectCryptoKey);
		assertNotNull(projectCryptoKey1);
		assertNotNull(projectCryptoKey2);

		assertEquals(Arrays.asList(//
				Maps.stringObjectMap(SoftwareFmConstants.projectCryptoKey, projectCryptoKey, LoginConstants.emailKey, email, GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.softwareFmIdKey, softwareFmId), //
				Maps.stringObjectMap(SoftwareFmConstants.projectCryptoKey, projectCryptoKey1, LoginConstants.emailKey, email1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus, LoginConstants.softwareFmIdKey, softwareFmId1), //
				Maps.stringObjectMap(SoftwareFmConstants.projectCryptoKey, projectCryptoKey2, LoginConstants.emailKey, email2, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus, LoginConstants.softwareFmIdKey, softwareFmId2)), //
				Iterables.list(groupsReader.users(groupId1, groupCryptoKey1)));
	}

	public void testButtonsEnabledStatusWhenNotMemberOfAnyGroup() {
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		assertFalse(buttons.accept.getEnabled());
		assertFalse(buttons.invite.getEnabled());
		assertTrue(buttons.create.getEnabled());
	}

	public void testButtonsEnabledStatusWhenNotSelectedAnyGroup() {
		addUserToGroup1AndGroup2();
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();

		assertFalse(buttons.accept.getEnabled());
		assertFalse(buttons.invite.getEnabled());
		assertTrue(buttons.create.getEnabled());
	}

	public void testButtonsEnabledStatusWhenAdmin() {
		createGroup(groupId1, groupCryptoKey1);
		addUserToGroup(softwareFmId, email1, groupId1, groupCryptoKey1, GroupConstants.adminStatus);

		createGroup(groupId2, groupCryptoKey2);
		addUserToGroup(softwareFmId, email2, groupId2, groupCryptoKey2, "someStatus2");
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();

		Table table = getMyGroupsTable(myGroupsComposite);
		table.select(0);
		table.notifyListeners(SWT.Selection, new Event());
		dispatchUntilQueueEmpty();

		assertFalse(buttons.accept.getEnabled());
		assertTrue(buttons.invite.getEnabled());
		assertTrue(buttons.create.getEnabled());
	}

	public void testButtonsEnabledStatusWhenInvited() {
		createGroup(groupId1, groupCryptoKey1);
		addUserToGroup(softwareFmId, email1, groupId1, groupCryptoKey1, GroupConstants.invitedStatus);

		createGroup(groupId2, groupCryptoKey2);
		addUserToGroup(softwareFmId, email2, groupId2, groupCryptoKey2, "someStatus2");
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();

		Table table = getMyGroupsTable(myGroupsComposite);
		table.select(0);
		table.notifyListeners(SWT.Selection, new Event());
		dispatchUntilQueueEmpty();

		assertTrue(buttons.accept.getEnabled());
		assertFalse(buttons.invite.getEnabled());
		assertTrue(buttons.create.getEnabled());
	}

	public void testCannotClickOkUntilGroupNameValidEmailListAndNoneEmptyEmail() {
		addUserToGroup1AndGroup2();

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		Swts.Buttons.press(buttons.create);
		dispatchUntilQueueEmpty();
		NameAndValuesEditorComposite editor = (NameAndValuesEditorComposite) masterDetailSocial.getDetailContent();
		checkLabelsMatch(editor.labels, "Group Name", "Email List", "Subject", "Email Pattern");
		checkTextMatches(editor.values, //
				"", //
				"<Type here a comma separated list of people you would like to invite to the group\nThe Email below will be sent with $email$ and $group$ replaced by your email, and the group name>",//
				"You are invited to join the SoftwareFM group $group$",//
				"$email$ has invited you to join the SoftwareFM - $group$\n\nAs you are not yet a member of SoftwareFM, you will need to download and install the SoftwareFM Eclipse plug in. You can find out how to do this by reading the Quick Guide at http://www.softwarefm.org/pages/downloads.php\n\nOnce you have downloaded and installed the SoftwareFM Eclipse plug in, simply login as a new user using the same email address as the one used to send you this email.\n\nOnce logged in you will be automatically an invited member to that group.");
		assertFalse(editor.getFooter().okButton().isEnabled());

		checkChange(editor, 0, "someNewGroupName");
		assertFalse(editor.getFooter().okButton().isEnabled());

		checkChange(editor, 1, "a@b.com,c@d.com");
		assertTrue(editor.getFooter().okButton().isEnabled());

		checkChange(editor, 2, "");
		assertFalse(editor.getFooter().okButton().isEnabled());

		checkChange(editor, 2, "someSubject");
		assertTrue(editor.getFooter().okButton().isEnabled());

		checkChange(editor, 3, "");
		assertFalse(editor.getFooter().okButton().isEnabled());

		checkChange(editor, 3, "someText");
		assertTrue(editor.getFooter().okButton().isEnabled());

		checkChange(editor, 0, "");
		assertFalse(editor.getFooter().okButton().isEnabled());
	}

	private void checkChange(NameAndValuesEditorComposite editor, int i, String newValue) {
		Control control = editor.values.getChildren()[i];
		Swts.setText(control, newValue);
	}

	public void testAccept() {
		fail();
	}

}
