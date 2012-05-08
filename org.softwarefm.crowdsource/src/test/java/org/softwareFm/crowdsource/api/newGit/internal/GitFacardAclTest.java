package org.softwareFm.crowdsource.api.newGit.internal;

import java.nio.channels.FileLock;

import org.easymock.EasyMock;
import org.softwareFm.crowdsource.api.git.TemporaryFileTest;
import org.softwareFm.crowdsource.api.newGit.IAccessControlList;
import org.softwareFm.crowdsource.api.newGit.ISingleRowReader;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;

public class GitFacardAclTest extends TemporaryFileTest {

	private IGitFacard gitFacard;
	private IAccessControlList acl;
	private ISingleRowReader reader;
	private String rl;
	private String repoRl;

	public void testLockUnLockDoNotUseAcl() {
		replayMocks();
		FileLock lock = gitFacard.lock("someRl");
		gitFacard.unLock(lock);
	}

	public void testInitUsesWriteAcl() {
		acl.write(repoRl);
		replayMocks();
		gitFacard.init(repoRl);
	}

	public void testGetFileUsesReadAcl() {
		acl.write(repoRl); // from init
		acl.read(rl); // from getFile
		replayMocks();
		gitFacard.init(repoRl);
		gitFacard.getFile(rl);
	}

	public void testPutFileUsesReadAcl() {
		acl.write(repoRl); // from init
		acl.write(rl); // from putFile
		replayMocks();
		gitFacard.init(repoRl);
		gitFacard.putFileReturningRepoRl(rl, "someText");
	}

	public void testAddAllDoesntUseAcl() {
		acl.write(repoRl); // from init
		replayMocks();
		gitFacard.init(repoRl);
		gitFacard.addAll(rl);
	}

	public void testFindRepoRlDoesntUseAcl(){
		acl.write(repoRl); // from init
		replayMocks();
		gitFacard.init(repoRl);
		gitFacard.findRepoRl(rl);
	}
	
	
	private void replayMocks() {
		EasyMock.replay(acl, reader);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		acl = EasyMock.createMock(IAccessControlList.class);
		gitFacard = IGitFacard.Utils.makeServerGitFacard(root, acl);
		reader = EasyMock.createMock(ISingleRowReader.class);
		repoRl = "repo";
		rl = "repo/someRl";
	}

	@Override
	protected void tearDown() throws Exception {
		EasyMock.verify(acl, reader);
		super.tearDown();
	}
}
