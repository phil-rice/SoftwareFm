package org.softwareFm.eclipse.mysoftwareFm.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.crowdsource.api.ITakeOnEnrichmentProvider;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.eclipse.mysoftwareFm.AbstractMyGroupsIntegrationTest;
import org.softwareFm.eclipse.mysoftwareFm.IdNameAndStatus;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups.MyGroupsButtons;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups.MyGroupsComposite;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.ISwtFunction1;
import org.softwareFm.swt.card.composites.CompositeWithCardMargin;
import org.softwareFm.swt.editors.NameAndValuesEditor.NameAndValuesEditorComposite;
import org.softwareFm.swt.swt.Swts;

public class GroupClientOperationsTest extends AbstractMyGroupsIntegrationTest {
	
	public void testButtonsEnabledStatusWhenNotMemberOfAnyGroup() {
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		dispatchUntilJobsFinished();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		assertFalse(buttons.accept.getEnabled());
		assertFalse(buttons.invite.getEnabled());
		assertTrue(buttons.create.getEnabled());
	}

	public void testAcceptInvite() throws InterruptedException {
		assertEquals(groupId0, createGroup(groupName0, groupCryptoKey0));
		addUserToGroup(softwareFmId0, email0, groupId0, groupCryptoKey0, GroupConstants.invitedStatus);

		final MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		dispatchUntilJobsFinished();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		Table beforeTable = getMyGroupsTable(myGroupsComposite);
		Swts.selectOnlyAndNotifyListener(beforeTable, 0);
		Thread.sleep(1000);
		dispatchUntilJobsFinished();

		assertTrue(buttons.accept.isEnabled());
		Swts.Buttons.press(buttons.accept);
		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				if (masterDetailSocial.getDetailContent() instanceof MyGroupsComposite) {
					MyGroupsComposite myGroupsComposite = (MyGroupsComposite) masterDetailSocial.getDetailContent();
					Table table = getMyGroupsTable(myGroupsComposite);
					if (table.getItemCount() > 0) {
						TableItem item = table.getItem(0);
						IdNameAndStatus idNameAndStatus = (IdNameAndStatus) item.getData();
						return idNameAndStatus.status.equals(GroupConstants.memberStatus);
					}
				}
				return false;
			}
		});
		IFunction3<IGroupsReader, IUserReader, IUserMembershipReader, Void> checkFn = new IFunction3<IGroupsReader, IUserReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader groupsReader, IUserReader userReader, IUserMembershipReader userMembershipReader) throws Exception {
				String projectCryptoKey = userReader.getUserProperty(softwareFmId0, userKey0, JarAndPathConstants.projectCryptoKey);
				assertEquals(GroupConstants.memberStatus, userMembershipReader.getMembershipProperty(softwareFmId0, userKey0, groupId0, GroupConstants.membershipStatusKey));
				assertEquals(Maps.stringObjectMap(JarAndPathConstants.projectCryptoKey, projectCryptoKey, //
						LoginConstants.emailKey, email0, //
						LoginConstants.softwareFmIdKey, softwareFmId0,//
						GroupConstants.membershipStatusKey, GroupConstants.memberStatus), Lists.getOnly(Iterables.list(groupsReader.users(groupId0, groupCryptoKey0))));
				return null;
			}
		};
		checkOnServerAndLocally(checkFn, new ISwtFunction1<Void, Void>() {
			@Override
			public Void apply(Void t) throws Exception {
				MyGroupsComposite afterMyGroupsComposite = (MyGroupsComposite) masterDetailSocial.getDetailContent();
				Table afterTable = getMyGroupsTable(afterMyGroupsComposite);
				assertEquals(1, afterTable.getItemCount());
				assertEquals(groupId0 + "Name", afterTable.getItem(0).getText(0));
				assertEquals("1", afterTable.getItem(0).getText(1));
				assertEquals(GroupConstants.memberStatus, afterTable.getItem(0).getText(2));
				return null;
			}
		});

	}

	public void testKickButtonRemovesUserFromGroup() {
		createGroup(groupId0, groupCryptoKey0);
		createUser(softwareFmId1, email1);
		createUser(softwareFmId2, email2);
		createUser(softwareFmId3, email3);
		addUserToGroup(softwareFmId0, email0, groupId0, groupCryptoKey0, GroupConstants.adminStatus);

		checkCanAddAndKick(new int[] { 1, 2 }, softwareFmId0, softwareFmId3);
		checkCanAddAndKick(new int[] { 1, 2 }, softwareFmId0, softwareFmId3);
		checkCanAddAndKick(new int[] { 1 }, softwareFmId0, softwareFmId2, softwareFmId3);
		checkCanAddAndKick(new int[] { 2 }, softwareFmId0, softwareFmId1, softwareFmId3);
		checkCanAddAndKick(new int[] { 3 }, softwareFmId0, softwareFmId1, softwareFmId2);
	}

	public void testLeaveIsEnabledIfAdminAndNoOtherMembers() {
		createGroup(groupId0, groupCryptoKey0);
		createUser(softwareFmId1, email1);
		addUserToGroup(softwareFmId0, email0, groupId0, groupCryptoKey0, GroupConstants.adminStatus);
		addUserToGroup(softwareFmId1, email1, groupId0, groupCryptoKey0, "someStatus2");

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		Table myGroupsTable = getMyGroupsTable(myGroupsComposite);

		Swts.selectAndNotifyListener(myGroupsTable, 0);
		dispatchUntilJobsFinished();
		assertFalse(buttons.leave.getEnabled()); //
		Table membershipTable = getMembershipTable(myGroupsComposite);

		Swts.selectOnlyAndNotifyListener(membershipTable, 1);
		dispatchUntilJobsFinished();
		pressKickButtonAndWaitUntilMembershipTableIsOfSize(buttons, 1);

		MyGroupsComposite afterMyGroupsComposite = (MyGroupsComposite) masterDetailSocial.getDetailContent();
		MyGroupsButtons afterButtons = afterMyGroupsComposite.getFooter();
		assertTrue(afterButtons.leave.getEnabled());
		Swts.selectAndNotifyListener(getMyGroupsTable(afterMyGroupsComposite), 0);// redundant as should be already selected, but doesn't hurt
		assertTrue(afterButtons.leave.getEnabled());
	}

	public void testCreateExceptionCausesTextPanelAndClickReturnsToMyGroups() {
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		Swts.Buttons.press(buttons.create);
		dispatchUntilJobsFinished();
		NameAndValuesEditorComposite editor = (NameAndValuesEditorComposite) masterDetailSocial.getDetailContent();
		checkChange(editor, 0, "someNewGroupName");
		checkChange(editor, 1, "notAnEmail");
		checkChange(editor, 2, "newSubject $email$/$group$");
		checkChange(editor, 3, "newMail $email$/$group$");
		pressOkEvenThoughBadAndCheckGetTextMessageAndEditorReturnsWithValues("Exception creating group. Click to try again\nclass java.lang.IllegalArgumentException/Invalid email notAnEmail",//
				"someNewGroupName", "notAnEmail", "newSubject $email$/$group$", "newMail $email$/$group$");
	}

	public void testLeaveIsEnabledIfNotAdmin() {
		addUserToGroup1AndGroup2();

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		assertFalse(buttons.leave.getEnabled());// no group selected
		Table myGroupsTable = getMyGroupsTable(myGroupsComposite);
		Swts.selectAndNotifyListener(myGroupsTable, 0);
		dispatchUntilJobsFinished();
		assertTrue(buttons.leave.getEnabled());
		Swts.selectAndNotifyListener(myGroupsTable, 1);
		assertTrue(buttons.leave.getEnabled());
	}

	public void testKickButtonIsOnlyEnabledWhenAdminAndSelectedNoneAdmin() {
		createGroup(groupId0, groupCryptoKey0);
		createUser(softwareFmId1, email1);

		addUserToGroup(softwareFmId0, email0, groupId0, groupCryptoKey0, GroupConstants.adminStatus);
		addUserToGroup(softwareFmId1, email1, groupId0, groupCryptoKey0, "someStatus2");

		createGroup(groupId1, groupCryptoKey1);
		addUserToGroup(softwareFmId0, email0, groupId1, groupCryptoKey1, "notAdmin");

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		assertFalse(buttons.kick.getEnabled());

		Table summaryTable = getMyGroupsTable(myGroupsComposite);

		Swts.selectOnlyAndNotifyListener(summaryTable, 0);
		assertFalse(buttons.kick.getEnabled());// nothing is selected on membership table

		Table membershipTable = getMembershipTable(myGroupsComposite);
		Swts.selectOnlyAndNotifyListener(membershipTable, 0);
		dispatchUntilJobsFinished();
		assertEquals(GroupConstants.adminStatus, getMembershipStatus(membershipTable, 0));// you are admin, they admin
		assertFalse(buttons.kick.getEnabled());

		Swts.selectOnlyAndNotifyListener(membershipTable, 1);
		dispatchUntilJobsFinished();
		assertEquals("someStatus2", getMembershipStatus(membershipTable, 1));// you are the admin, they are not admin
		assertTrue(buttons.kick.getEnabled());

		Swts.selectOnlyAndNotifyListener(summaryTable, 1);
		dispatchUntilJobsFinished();
		assertFalse(buttons.kick.getEnabled());// nothing is selected on membership table

		Swts.selectOnlyAndNotifyListener(membershipTable, 0);
		dispatchUntilJobsFinished();
		assertEquals("notAdmin", getMembershipStatus(membershipTable, 0));// you are not admin, they are not admin
		assertFalse(buttons.kick.getEnabled());
	}

	public void testCannotKickIfOneOfMultipleSelectIsAdmin() {
		createGroup(groupId0, groupCryptoKey0);
		createUser(softwareFmId1, email1);
		createUser(softwareFmId2, email2);
		createUser(softwareFmId3, email3);

		addUserToGroup(softwareFmId0, email0, groupId0, groupCryptoKey0, GroupConstants.adminStatus);
		addUserToGroup(softwareFmId1, email1, groupId0, groupCryptoKey0, "someStatus1");
		addUserToGroup(softwareFmId2, email2, groupId0, groupCryptoKey0, "someStatus2");
		addUserToGroup(softwareFmId3, email3, groupId0, groupCryptoKey0, GroupConstants.adminStatus);

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table summaryTable = getMyGroupsTable(myGroupsComposite);
		Swts.selectOnlyAndNotifyListener(summaryTable, 0);

		checkCanKick(myGroupsComposite, false, 0);
		checkCanKick(myGroupsComposite, false, 0, 1);
		checkCanKick(myGroupsComposite, false, 0, 1, 2, 3);
		checkCanKick(myGroupsComposite, true, 1, 2);
		checkCanKick(myGroupsComposite, false, 1, 2, 3);
		checkCanKick(myGroupsComposite, true, 1);
		checkCanKick(myGroupsComposite, true, 2);
		checkCanKick(myGroupsComposite, false, 3);

	}

	private void checkCanKick(MyGroupsComposite myGroupsComposite, boolean expected, int... is) {
		dispatchUntilJobsFinished();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		Table membershipTable = getMembershipTable(myGroupsComposite);
		Swts.selectAndNotifyListener(membershipTable, is);
		dispatchUntilJobsFinished();
		assertEquals(expected, buttons.kick.getEnabled());

	}

	@SuppressWarnings("unchecked")
	private Object getMembershipStatus(Table membershipTable, int i) {
		TableItem item = membershipTable.getItem(i);
		Map<String, Object> data = (Map<String, Object>) item.getData();
		return data.get(GroupConstants.membershipStatusKey);
	}

	private void checkCanAddAndKick(int[] kick, final String... expected) {
		addUserToGroup(softwareFmId1, email1, groupId0, groupCryptoKey0, "someStatus1");
		addUserToGroup(softwareFmId2, email2, groupId0, groupCryptoKey0, "someStatus2");
		addUserToGroup(softwareFmId3, email3, groupId0, groupCryptoKey0, "someStatus3");

		System.out.println("displayMySoftwareClickMyGroup");
		final MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		assertFalse(buttons.kick.getEnabled());
		Table summaryTable = getMyGroupsTable(myGroupsComposite);
		System.out.println("about to select summary table(0)" + Arrays.asList(summaryTable.getItems()));

		Swts.selectOnlyAndNotifyListener(summaryTable, 0);
		dispatchUntilJobsFinished();
		assertFalse(buttons.kick.getEnabled());// nothing is selected on membership table

		System.out.println("about to select summary table(" + Arrays.asList(kick) + "): " + Arrays.asList(summaryTable.getItems()));
		final Table membershipTable = getMembershipTable(myGroupsComposite);
		Swts.selectAndNotifyListener(membershipTable, kick);
		dispatchUntilJobsFinished();
		System.out.println("about to kick");
		pressKickButtonAndWaitUntilMembershipTableIsOfSize(buttons, expected.length);
		System.out.println("finished kick processing");
		MyGroupsComposite afterMyGroupsComposite = (MyGroupsComposite) masterDetailSocial.getDetailContent();
		final Table afterMembershipTable = getMembershipTable(afterMyGroupsComposite);
		MyGroupsButtons afterButtons = afterMyGroupsComposite.getFooter();
		List<String> actual = getMembershipIds(afterMembershipTable);
		assertEquals(Arrays.asList(expected), actual);

		for (int i = 1; i < afterMembershipTable.getItemCount(); i++)
			afterMembershipTable.select(i);
		afterMembershipTable.notifyListeners(SWT.Selection, new Event());
		pressKickButtonAndWaitUntilMembershipTableIsOfSize(afterButtons, 1);
	}

	private void pressKickButtonAndWaitUntilMembershipTableIsOfSize(MyGroupsButtons buttons, final int length) {
		System.out.println("pressKickButtonAndWaitUntilMembershipTableIsOfSize: " + masterDetailSocial.getDetailContent() + " " + length);
		assertTrue(buttons.kick.getEnabled());
		Swts.Buttons.press(buttons.kick);
		dispatchUntilJobsFinished();
		System.out.println("kickButtonWasPressedWaitUntilMembershipTableIsOfSize: " + masterDetailSocial.getDetailContent() + " " + length);
		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				System.out.println(masterDetailSocial.getDetailContent());
				if (masterDetailSocial.getDetailContent() instanceof MyGroupsComposite) {
					MyGroupsComposite myGroupsComposite = (MyGroupsComposite) masterDetailSocial.getDetailContent();
					final Table membershipTable = getMembershipTable(myGroupsComposite);
					Swts.layoutDump(myGroupsComposite);
					System.out.println("membershipTable: " + membershipTable + " " + (membershipTable == null ? "" : membershipTable.getItemCount()));
					if (membershipTable == null)
						return false;
					int itemCount = membershipTable.getItemCount();
					System.out.println("expected: " + length + " actual: " + itemCount + " myGroupsComposite: " + myGroupsComposite);
					return itemCount == length;
				}
				return false;
			}
		});
	}

	@SuppressWarnings("unchecked")
	private List<String> getMembershipIds(Table membershipTable) {
		List<String> actual = Lists.newList();
		for (TableItem item : membershipTable.getItems()) {
			Map<String, Object> data = (Map<String, Object>) item.getData();
			actual.add((String) data.get(LoginConstants.softwareFmIdKey));
		}
		return actual;
	}

	public void testInviteExceptionCausesTextPanelAndClickReturnsToMyGroups() {
		createGroup(groupName0, groupCryptoKey0);
		addUserToGroup(softwareFmId0, email0, groupId0, groupCryptoKey0, GroupConstants.adminStatus);
		String groupName = groupId0 + "Name";

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table beforeTable = getMyGroupsTable(myGroupsComposite);
		Swts.selectOnlyAndNotifyListener(beforeTable, 0);
		dispatchUntilJobsFinished();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		assertTrue(buttons.invite.isEnabled());
		Swts.Buttons.press(buttons.invite);
		dispatchUntilJobsFinished();

		NameAndValuesEditorComposite editor = (NameAndValuesEditorComposite) masterDetailSocial.getDetailContent();

		assertFalse(((Text) editor.getEditor().getChildren()[1]).getEditable());
		checkChange(editor, 1, "Not an email");
		checkChange(editor, 2, "newSubject $email$/$group$");
		checkChange(editor, 3, "newMail $email$/$group$");
		pressOkEvenThoughBadAndCheckGetTextMessageAndEditorReturnsWithValues("Exception inviting to group. Click to try again\nclass java.lang.IllegalArgumentException/Invalid email Not an email",//
				groupName, "Not an email", "newSubject $email$/$group$", "newMail $email$/$group$");
	}

	protected void pressOkEvenThoughBadAndCheckGetTextMessageAndEditorReturnsWithValues(String exceptionMessage, String... values) {
		NameAndValuesEditorComposite editor = (NameAndValuesEditorComposite) masterDetailSocial.getDetailContent();
		assertFalse(editor.getFooter().okButton().isEnabled());
		editor.getFooter().ok(); // press it anyway!

		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control detailContent = masterDetailSocial.getDetailContent();
				return detailContent instanceof CompositeWithCardMargin;
			}
		});
		CompositeWithCardMargin compositeWithCardMargin = (CompositeWithCardMargin) masterDetailSocial.getDetailContent();
		StyledText text = Swts.getDescendant(compositeWithCardMargin, 1, 0, 0);
		assertEquals(exceptionMessage, text.getText());
		assertFalse(text.getEditable());
		text.notifyListeners(SWT.MouseUp, new Event());
		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control detailContent = masterDetailSocial.getDetailContent();
				return detailContent instanceof NameAndValuesEditorComposite;
			}
		});
		NameAndValuesEditorComposite afterEditor = (NameAndValuesEditorComposite) masterDetailSocial.getDetailContent();
		Swts.checkTextMatches(afterEditor.getEditor(), values);
	}

	@SuppressWarnings("unchecked")
	public void testInvite() {
		createGroup(groupName0, groupCryptoKey0);
		addUserToGroup(softwareFmId0, email0, groupId0, groupCryptoKey0, GroupConstants.adminStatus);
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		Table beforeTable = getMyGroupsTable(myGroupsComposite);
		Swts.selectOnlyAndNotifyListener(beforeTable, 0);
		dispatchUntilJobsFinished();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		assertTrue(buttons.invite.isEnabled());
		Swts.Buttons.press(buttons.invite);
		dispatchUntilJobsFinished();

		NameAndValuesEditorComposite editor = (NameAndValuesEditorComposite) masterDetailSocial.getDetailContent();
		assertFalse(((Text) editor.getEditor().getChildren()[1]).getEditable());
		checkChange(editor, 1, email1 + "," + email2);
		checkChange(editor, 2, "newSubject $email$/$group$");
		checkChange(editor, 3, "newMail $email$/$group$");
		assertTrue(editor.getFooter().okButton().isEnabled());

		editor.getFooter().ok();
		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control detailContent = masterDetailSocial.getDetailContent();
				return detailContent instanceof MyGroupsComposite;
			}
		});

		Table afterTable = getMyGroupsTable((MyGroupsComposite) masterDetailSocial.getDetailContent());
		Swts.checkTable(afterTable, 0, new IdNameAndStatus(groupId0, groupName0, GroupConstants.adminStatus), groupName0, "3", GroupConstants.adminStatus);
		assertEquals(1, afterTable.getItemCount());
		assertEquals(0, afterTable.getSelectionIndex());

		assertEquals(Lists.times(2, email0), mailer.froms);
		assertEquals(Arrays.asList(email1, email2), mailer.tos);
		assertEquals(Arrays.asList("newSubject " + email0 + "/" + groupName0, "newSubject " + email0 + "/" + groupName0), mailer.subjects);
		assertEquals(Arrays.asList("newMail " + email0 + "/" + groupName0, "newMail " + email0 + "/" + groupName0), mailer.messages);
		IFunction3<IGroupsReader, IUserReader, IUserMembershipReader, Void> checkFn = new IFunction3<IGroupsReader, IUserReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader groupsReader, IUserReader userReader, IUserMembershipReader userMembershipReader) throws Exception {
				assertEquals(groupName0, groupsReader.getGroupProperty(groupId0, groupCryptoKey0, GroupConstants.groupNameKey));

				assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId0, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus)), userMembershipReader.walkGroupsFor(softwareFmId0, userKey0));
				assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId0, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus)), userMembershipReader.walkGroupsFor(softwareFmId1, userKey1));
				assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId0, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus)), userMembershipReader.walkGroupsFor(softwareFmId2, userKey2));

				String projectCryptoKey = userReader.getUserProperty(softwareFmId0, userKey0, JarAndPathConstants.projectCryptoKey);
				String projectCryptoKey1 = userReader.getUserProperty(softwareFmId1, userKey1, JarAndPathConstants.projectCryptoKey);
				String projectCryptoKey2 = userReader.getUserProperty(softwareFmId2, userKey2, JarAndPathConstants.projectCryptoKey);

				assertNotNull(projectCryptoKey);
				assertNotNull(projectCryptoKey1);
				assertNotNull(projectCryptoKey2);

				assertEquals(Arrays.asList(//
						Maps.stringObjectMap(JarAndPathConstants.projectCryptoKey, projectCryptoKey, LoginConstants.emailKey, email0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.softwareFmIdKey, softwareFmId0), //
						Maps.stringObjectMap(JarAndPathConstants.projectCryptoKey, projectCryptoKey1, LoginConstants.emailKey, email1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus, LoginConstants.softwareFmIdKey, softwareFmId1), //
						Maps.stringObjectMap(JarAndPathConstants.projectCryptoKey, projectCryptoKey2, LoginConstants.emailKey, email2, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus, LoginConstants.softwareFmIdKey, softwareFmId2)), //
						Iterables.list(groupsReader.users(groupId0, groupCryptoKey0)));
				return null;
			}
		};
		checkOnServerAndLocally(checkFn, Functions.<Void, Void> identity());
	}

	@SuppressWarnings("unchecked")
	public void testDisplaysCreateActuallyCreatesSendsEmailsAndDisplaysMyGroupsAtEnd() {
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		Swts.Buttons.press(buttons.create);
		dispatchUntilJobsFinished();
		NameAndValuesEditorComposite editor = (NameAndValuesEditorComposite) masterDetailSocial.getDetailContent();
		checkChange(editor, 0, "someNewGroupName");
		checkChange(editor, 1, email1 + "," + email2);
		checkChange(editor, 2, "newSubject $email$/$group$");
		checkChange(editor, 3, "newMail $email$/$group$");
		assertTrue(editor.getFooter().okButton().isEnabled());

		editor.getFooter().ok();
		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control detailContent = masterDetailSocial.getDetailContent();
				return detailContent instanceof MyGroupsComposite;
			}
		});

		Table table = getMyGroupsTable((MyGroupsComposite) masterDetailSocial.getDetailContent());
		Swts.checkTable(table, 0, new IdNameAndStatus(groupId0, "someNewGroupName", GroupConstants.adminStatus), "someNewGroupName", "3", GroupConstants.adminStatus);
		assertEquals(1, table.getItemCount());
		assertEquals(0, table.getSelectionIndex());

		assertEquals(Lists.times(2, email0), mailer.froms);
		assertEquals(Arrays.asList(email1, email2), mailer.tos);
		assertEquals(Arrays.asList("newSubject " + email0 + "/someNewGroupName", "newSubject " + email0 + "/someNewGroupName"), mailer.subjects);
		assertEquals(Arrays.asList("newMail " + email0 + "/someNewGroupName", "newMail " + email0 + "/someNewGroupName"), mailer.messages);

		IFunction3<IGroupsReader, IUserReader, IUserMembershipReader, Void> checkFn = new IFunction3<IGroupsReader, IUserReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader groupsReader, IUserReader userReader, IUserMembershipReader userMembershipReader) throws Exception {
				assertEquals("someNewGroupName", groupsReader.getGroupProperty(groupId0, groupCryptoKey0, GroupConstants.groupNameKey));

				assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId0, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus)), userMembershipReader.walkGroupsFor(softwareFmId0, userKey0));
				assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId0, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus)), userMembershipReader.walkGroupsFor(softwareFmId1, userKey1));
				assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId0, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus)), userMembershipReader.walkGroupsFor(softwareFmId2, userKey2));

				assertEquals(Arrays.asList(//
						Maps.stringObjectMap(JarAndPathConstants.projectCryptoKey, projectCryptoKey0, LoginConstants.emailKey, email0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.softwareFmIdKey, softwareFmId0), //
						Maps.stringObjectMap(JarAndPathConstants.projectCryptoKey, projectCryptoKey1, LoginConstants.emailKey, email1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus, LoginConstants.softwareFmIdKey, softwareFmId1), //
						Maps.stringObjectMap(JarAndPathConstants.projectCryptoKey, projectCryptoKey2, LoginConstants.emailKey, email2, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus, LoginConstants.softwareFmIdKey, softwareFmId2)), //
						Iterables.list(groupsReader.users(groupId0, groupCryptoKey0)));
				return null;
			}
		};
		checkOnServerAndLocally(checkFn, Functions.<Void, Void> identity());
	}

	private void checkOnServerAndLocally(IFunction3<IGroupsReader, IUserReader, IUserMembershipReader, Void> checkFn, final IFunction1<Void, Void> callbackFn) {
		final long timeOutMs = getServerConfig().timeOutMs;
		ICallback<Void> callback = new ICallback<Void>() {
			@Override
			public void process(final Void t) throws Exception {
				if (Thread.currentThread() == display.getThread())
					callbackFn.apply(t);
				else {
					final CountDownLatch latch = new CountDownLatch(1);
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								callbackFn.apply(t);
							} catch (Exception e) {
								throw WrappedException.wrap(e);
							} finally {
								latch.countDown();
							}
						}
					});
					latch.await(timeOutMs, TimeUnit.MILLISECONDS);
				}
			}
		};
		getServerApi().makeContainer().accessWithCallback(IGroupsReader.class, IUserReader.class, IUserMembershipReader.class, checkFn, callback).get();
		getLocalApi().makeContainer().accessWithCallback(IGroupsReader.class, IUserReader.class, IUserMembershipReader.class, checkFn, callback).get();
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
		createGroup(groupId0, groupCryptoKey0);
		addUserToGroup(softwareFmId0, email1, groupId0, groupCryptoKey0, GroupConstants.adminStatus);

		createGroup(groupId1, groupCryptoKey1);
		addUserToGroup(softwareFmId0, email2, groupId1, groupCryptoKey1, "someStatus2");
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();

		Table table = getMyGroupsTable(myGroupsComposite);
		table.select(0);
		table.notifyListeners(SWT.Selection, new Event());
		dispatchUntilJobsFinished();

		assertFalse(buttons.accept.getEnabled());
		assertTrue(buttons.invite.getEnabled());
		assertTrue(buttons.create.getEnabled());
	}

	public void testButtonsEnabledStatusWhenInvited() {
		createGroup(groupId0, groupCryptoKey0);
		addUserToGroup(softwareFmId0, email1, groupId0, groupCryptoKey0, GroupConstants.invitedStatus);

		createGroup(groupId1, groupCryptoKey1);
		addUserToGroup(softwareFmId0, email2, groupId1, groupCryptoKey1, "someStatus2");
		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();

		Table table = getMyGroupsTable(myGroupsComposite);
		Swts.selectOnlyAndNotifyListener(table, 0);
		dispatchUntilJobsFinished();
		assertTrue(buttons.accept.getEnabled());
		assertFalse(buttons.invite.getEnabled());
		assertTrue(buttons.create.getEnabled());
	}

	public void testCannotClickOkUntilGroupNameValidEmailListAndNoneEmptyEmail() {
		addUserToGroup1AndGroup2();

		MyGroupsComposite myGroupsComposite = displayMySoftwareClickMyGroup();
		MyGroupsButtons buttons = myGroupsComposite.getFooter();
		Swts.Buttons.press(buttons.create);
		dispatchUntilJobsFinished();
		NameAndValuesEditorComposite editor = (NameAndValuesEditorComposite) masterDetailSocial.getDetailContent();
		Swts.checkLabelsMatch(editor.getEditor(), "Group Name", "Email List", "Subject", "Email Pattern");
		Swts.checkTextMatches(editor.getEditor(), //
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
		Control control = editor.getEditor().getChildren()[i * 2 + 1];
		Swts.setText(control, newValue);
	}

	@Override
	protected ITakeOnEnrichmentProvider getTakeOnEnrichment() {
		return ITakeOnEnrichmentProvider.Utils.enrichmentWithUserProperty(JarAndPathConstants.projectCryptoKey);
	}
}
