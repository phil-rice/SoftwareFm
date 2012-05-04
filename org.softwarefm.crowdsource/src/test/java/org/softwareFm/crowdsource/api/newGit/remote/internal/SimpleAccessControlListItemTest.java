package org.softwareFm.crowdsource.api.newGit.remote.internal;

import org.softwareFm.crowdsource.api.newGit.exceptions.CannotWriteException;
import org.softwareFm.crowdsource.api.newGit.internal.RawSingleSource;
import org.softwareFm.crowdsource.api.newGit.internal.RepoTest;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class SimpleAccessControlListItemTest extends RepoTest {
	private SimpleAccessControlList acl;

	// no longer needed
	public void _testCannotUpdateListItemIfNotLegalPrefix() {
		checkCannotUpdate("repo/url1", 0, "Cannot write to repo/url1");
		checkCannotUpdate("repo/url1", 2, "Cannot write to repo/url1");
		checkCannotUpdate("repo/doesntExist", 0, "Cannot write to repo/doesntExist");
	}

	public void testCanUpdateListItemIfNoUserInItem() {
		checkCanUpdate("repo/prefix1/url1", 0);
		checkCanUpdate("repo/prefix1/url1", 1);
	}

	public void testCanUpdateListItemIfUserInItemAndAreUser() {
		checkCanUpdate("repo/prefix1/url1", 2);
		checkCanUpdate("repo/prefix1/url1", 3);
	}

	public void testCannotUpdateListItemIfUserInItemAndNotUser() {
		checkCannotUpdate("repo/prefix1/url1", 4, "Cannot update list item at repo/prefix1/url1 with index 4 as user is uId1 and required user is uId2");
		checkCannotUpdate("repo/prefix1/url1", 5, "Cannot update list item at repo/prefix1/url1 with index 5 as user is uId1 and required user is uId2");
	}

	// no longer needed
	public void _testCannotDeleteListItemIfNotLegalPrefix() {
		checkCannotDelete("repo/url1", 0, "Cannot write to repo/url1");
		checkCannotDelete("repo/url1", 2, "Cannot write to repo/url1");
		checkCannotDelete("repo/doesntExist", 0, "Cannot write to repo/doesntExist");
	}

	public void testCanDeleteListItemIfNoUserInItem() {
		checkCanDelete("repo/prefix1/url1", 0);
		checkCanDelete("repo/prefix1/url1", 1);
	}

	public void testCanDeleteListItemIfUserInItemAndAreUser() {
		checkCanDelete("repo/prefix1/url1", 2);
		checkCanDelete("repo/prefix1/url1", 3);
	}

	public void testCannotDeleteListItemIfUserInItemAndNotUser() {
		checkCannotDelete("repo/prefix1/url1", 4, "Cannot delete list item at repo/prefix1/url1 with index 4 as user is uId1 and required user is uId2");
		checkCannotDelete("repo/prefix1/url1", 5, "Cannot delete list item at repo/prefix1/url1 with index 5 as user is uId1 and required user is uId2");
	}

	private void checkCanUpdate(String rl, int index) {
		acl.updateListItem(linkedRepoData, new RawSingleSource(rl), index);

	}

	private void checkCannotUpdate(final String rl, final int index, String message) {
		Tests.assertThrowsWithMessage(message, CannotWriteException.class, new Runnable() {
			@Override
			public void run() {
				acl.updateListItem(linkedRepoData, new RawSingleSource(rl), index);
			}
		});

	}

	private void checkCanDelete(String rl, int index) {
		acl.deleteListItem(linkedRepoData, new RawSingleSource(rl), index);

	}

	private void checkCannotDelete(final String rl, final int index, String message) {
		Tests.assertThrowsWithMessage(message, CannotWriteException.class, new Runnable() {
			@Override
			public void run() {
				acl.deleteListItem(linkedRepoData, new RawSingleSource(rl), index);
			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		acl = new SimpleAccessControlList(user1Data, "repo/prefix1", "repo/prefix2");
		initRepos(remoteFacard, "repo");
		putFile(remoteFacard, "repo/url1", null, v11, v12, v11User1, v12User1, v11User2, v11User2);
		putFile(remoteFacard, "repo/prefix1/url1", null, v11, v12, v11User1, v12User1, v11User2, v11User2);
		putFile(remoteFacard, "repo/prefix2/url1", null, v11, v12, v11User1, v12User1, v11User2, v11User2);
		addAllAndCommit(remoteFacard, "repo");
	}

	public static void main(String[] args) {
		while (true)
			Tests.executeTest(SimpleAccessControlListItemTest.class);
	}
}
