package org.softwareFm.crowdsource.api.newGit.remote.internal;

import java.text.MessageFormat;

import org.softwareFm.crowdsource.api.newGit.exceptions.AclMessages;
import org.softwareFm.crowdsource.api.newGit.exceptions.CannotDeleteException;
import org.softwareFm.crowdsource.api.newGit.exceptions.CannotWriteException;
import org.softwareFm.crowdsource.api.newGit.internal.RepoTest;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class SimpleAccessControlListReadWriteDeleteTest extends RepoTest {

	private SimpleAccessControlList acl;

	public void testCanReadAnything() {
		checkCanRead("any/thing");
		checkCanRead("prefix1");
		checkCanRead("prefix1/any/thing");
		checkCanRead("prefix1/any/thing/data.json");
	}

	public void testCanWriteToNamedPrefixes() {
		checkCanWrite("prefix1");
		checkCanWrite("prefix1/any");
		checkCanWrite("prefix1/any/thing");
		checkCanWrite("prefix1/any/thing.txt");
		checkCanWrite("prefix2");
		checkCanWrite("prefix2/any/thing.txt");
	}

	public void testCannotWriteIfNotInPrefixList() {
		checkCannotWrite("prefix");
		checkCannotWrite("");
		checkCannotWrite("any");
		checkCannotWrite("any/thing");
		checkCannotWrite("any/thing/data.txt");
	}

	public void testCannotDelete() {
		checkCannotDelete("prefix");
		checkCannotDelete("prefix1");
		checkCannotDelete("prefix2");
		checkCannotDelete("");
		checkCannotDelete("any");
		checkCannotDelete("any/thing/data.txt");
	}

	private void checkCanRead(String rl) {
		acl.read(rl);
	}

	private void checkCanWrite(String rl) {
		acl.write(rl);
	}

	private void checkCannotDelete(final String rl) {
		Tests.assertThrowsWithMessage(MessageFormat.format(AclMessages.cannotDeleteException, rl), CannotDeleteException.class, new Runnable() {
			@Override
			public void run() {
				acl.delete(rl);
			}
		});
	}

	private void checkCannotWrite(final String rl) {
		Tests.assertThrowsWithMessage(MessageFormat.format(AclMessages.cannotWriteException, rl), CannotWriteException.class, new Runnable() {
			@Override
			public void run() {
				acl.write(rl);
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		acl = new SimpleAccessControlList(user1Data, "prefix1", "prefix2");
	}

}
