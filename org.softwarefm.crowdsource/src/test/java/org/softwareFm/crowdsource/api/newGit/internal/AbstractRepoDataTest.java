package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.IRepoReader;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.RepoLocation;
import org.softwareFm.crowdsource.api.newGit.SourcedMap;
import org.softwareFm.crowdsource.api.newGit.exceptions.CannotChangeTwiceException;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.tests.Tests;

@SuppressWarnings("unchecked")
abstract public class AbstractRepoDataTest extends RepoTest {

	protected final String abxFile = "a/b/x/data.txt";
	protected final String abyFile = "a/b/y/data.txt";
	protected final String acxFile = "a/c/x/data.txt";
	protected final String acyFile = "a/c/y/data.txt";

	protected ISingleSource abxSource;
	protected ISingleSource abySource;
	protected ISingleSource acxSource;
	protected ISingleSource acySource;

	abstract protected void putFilePrim(String rl, String lines);

	protected void putFile(String rl, String lines) {
		cloneIfNeeded();
		putFilePrim(rl, lines);
	}

	private void cloneIfNeeded() {
		if (!haveCloned) {
			IGitFacard.Utils.clone(linkedFacard, "a/b");
			IGitFacard.Utils.clone(linkedFacard, "a/c");
		}
		haveCloned = true;
	}

	boolean haveCloned;

	public void testSetup() {
		assertNotNull(abxSource);
		assertNotNull(abySource);
		assertNotNull(acxSource);
		assertNotNull(acySource);
	}

	public void testFindRepositoryReturnsTheRemoteLocationIfLocalDoesntExist() {
		RepoLocation abRemote = RepoLocation.remote(localRoot, "a/b");
		RepoLocation acRemote = RepoLocation.remote(localRoot, "a/c");

		assertEquals(abRemote, linkedRepoData.findRepository(abxSource));
		assertEquals(abRemote, linkedRepoData.findRepository(abySource));
		assertEquals(acRemote, linkedRepoData.findRepository(acxSource));
		assertEquals(acRemote, linkedRepoData.findRepository(acySource));
	}

	public void testFindRepositoryReturnsTheLocalLocationIfLocalExists() {
		cloneIfNeeded();

		RepoLocation abLocal = RepoLocation.local(localRoot, "a/b");
		RepoLocation acLocal = RepoLocation.local(localRoot, "a/c");

		assertEquals(abLocal, linkedRepoData.findRepository(abxSource));
		assertEquals(abLocal, linkedRepoData.findRepository(abySource));
		assertEquals(acLocal, linkedRepoData.findRepository(acxSource));
		assertEquals(acLocal, linkedRepoData.findRepository(acySource));
	}

	public void testFindRepositories() {
		IGitFacard.Utils.clone(linkedFacard, "a/b");

		RepoLocation abrepo = RepoLocation.local(localRoot, "a/b");
		RepoLocation acrepo = RepoLocation.remote(localRoot, "a/c");

		assertEquals(Sets.makeSet(), IRepoData.Utils.findRepositories(linkedRepoData, new SourcesMock(linkedRepoData, "", "")));
		assertEquals(Sets.makeSet(abrepo), IRepoData.Utils.findRepositories(linkedRepoData, new SourcesMock(linkedRepoData, "", "", abxSource)));
		assertEquals(Sets.makeSet(abrepo), IRepoData.Utils.findRepositories(linkedRepoData, new SourcesMock(linkedRepoData, "", "", abxSource, abySource)));
		assertEquals(Sets.makeSet(abrepo, acrepo), IRepoData.Utils.findRepositories(linkedRepoData, new SourcesMock(linkedRepoData, "", "", abxSource, acxSource)));
		assertEquals(Sets.makeSet(abrepo, acrepo), IRepoData.Utils.findRepositories(linkedRepoData, new SourcesMock(linkedRepoData, "", "", abxSource, abySource, abySource, acySource)));
	}

	public void testReadAllRows() {
		putFile(abxFile, v11v12Json);

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(linkedRepoData, abxSource));
		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(linkedRepoData, abxSource));
	}

	public void testCountLines() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v11v12v21v22Json);
		assertEquals(2, IRepoReader.Utils.countLines(linkedRepoData, abxSource));
		assertEquals(4, IRepoReader.Utils.countLines(linkedRepoData, acxSource));
	}

	public void testCountLinesSources() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v11v12v21v22Json);
		assertEquals(Maps.makeMap(), IRepoReader.Utils.countLines(linkedRepoData, new SourcesMock(linkedRepoData, "", "")));
		assertEquals(Maps.makeMap(abxSource, 2), IRepoReader.Utils.countLines(linkedRepoData, new SourcesMock(linkedRepoData, "", "", abxSource)));
		assertEquals(Maps.makeMap(abxSource, 2, acxSource, 4), IRepoReader.Utils.countLines(linkedRepoData, new SourcesMock(linkedRepoData, "", "", abxSource, acxSource)));
	}

	public void testCountLinesWithNoDataReturns0() {
		assertEquals(0, IRepoReader.Utils.countLines(linkedRepoData, abxSource));
		assertEquals(0, IRepoReader.Utils.countLines(linkedRepoData, acxSource));
	}

	public void testReadRawCachesResults() {
		putFile(abxFile, v11v12Json);

		List<String> first = linkedRepoData.readRaw(abxSource);
		List<String> second = linkedRepoData.readRaw(abxSource);
		assertEquals(first.size(), second.size());
		for (int i = 0; i < first.size(); i++)
			assertSame(first.get(i), second.get(i));
	}

	public void testReadAllRowsCachesResults() {
		putFile(abxFile, v11v12Json);

		List<Map<String, Object>> first = IRepoReader.Utils.readAllRows(linkedRepoData, abxSource);
		List<Map<String, Object>> second = IRepoReader.Utils.readAllRows(linkedRepoData, abxSource);
		assertEquals(first.size(), second.size());
		for (int i = 0; i < first.size(); i++)
			assertSame(first.get(i), second.get(i));
	}

	public void testReadAllRowsAndReadRowsAreSameValue() {
		putFile(abxFile, v11v12Json);

		List<Map<String, Object>> all = IRepoReader.Utils.readAllRows(linkedRepoData, abxSource);
		for (int i = 0; i < all.size(); i++)
			assertSame(all.get(i), linkedRepoData.readRow(abxSource, i));
	}

	public void testReadFirstRow() {
		putFile(abxFile, v11v12Json);

		List<Map<String, Object>> all = IRepoReader.Utils.readAllRows(linkedRepoData, abxSource);
		assertSame(all.get(0), IRepoReader.Utils.readFirstRow(linkedRepoData, abxSource));
		assertEquals(Maps.emptyStringObjectMap(), IRepoReader.Utils.readFirstRow(linkedRepoData, acxSource));

	}

	public void testReadLockedTheRepo() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v21v22Json);

		assertEquals(0, linkedRepoData.locks().size());

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(linkedRepoData, abxSource));
		assertEquals(1, linkedRepoData.locks().size());
		assertNotNull(linkedRepoData.locks().get("a/b"));

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(linkedRepoData, abxSource));
		assertEquals(1, linkedRepoData.locks().size());
		assertNotNull(linkedRepoData.locks().get("a/b"));

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(linkedRepoData, abxSource));
		assertEquals(1, linkedRepoData.locks().size());
		assertNotNull(linkedRepoData.locks().get("a/b"));

		assertEquals(v21v22List, IRepoReader.Utils.readAllRows(linkedRepoData, acxSource));
		assertEquals(2, linkedRepoData.locks().size());
		assertNotNull(linkedRepoData.locks().get("a/b"));
		assertNotNull(linkedRepoData.locks().get("a/c"));
	}

	public void testCommitReleaseslocks() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v21v22Json);

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(linkedRepoData, abxSource));
		assertEquals(v21v22List, IRepoReader.Utils.readAllRows(linkedRepoData, acxSource));

		linkedRepoData.setCommitMessage("someMessage");
		linkedRepoData.commit();
		assertEquals(0, linkedRepoData.locks().size());
	}


	public void testCommitClearsUsable(){
		linkedRepoData.setCommitMessage("someMessage");
		assertTrue(linkedRepoData.usable());
		linkedRepoData.commit();
		assertFalse(linkedRepoData.usable());
		
	}
	public void testRollbackClearsUsable(){
		linkedRepoData.setCommitMessage("someMessage");
		assertTrue(linkedRepoData.usable());
		linkedRepoData.rollback();
		assertFalse(linkedRepoData.usable());
		
	}
	public void testRollbackReleaseslocks() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v21v22Json);

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(linkedRepoData, abxSource));
		assertEquals(v21v22List, IRepoReader.Utils.readAllRows(linkedRepoData, acxSource));
		linkedRepoData.rollback();
		assertEquals(0, linkedRepoData.locks().size());
	}

	public void testCommitCausesHasPulledToBeCommitted() {
		linkedRepoData.readRaw(abxSource);

		assertFalse(hasPulledRaw.contains("a/b"));
		assertTrue(hasPulled.contains("a/b"));
		
		linkedRepoData.commit();
		assertTrue(hasPulledRaw.contains("a/b"));
	}

	public void testRollbackCausesHasPulledToBeRollbacked() {
		putFile(abxFile, v11v12Json);
		linkedRepoData.rollback();
		assertFalse(hasPulledRaw.contains("a/b"));
		// note that this is actually not a very good test. Mostly it is checking that we didn't call rollback
	}

	public void testAppendDoesntChangeBeforeCommitButAddsToAppendList() {
		putFile(abxFile, v11v12Json);
		linkedRepoData.append(abxSource, v21);
		linkedRepoData.append(abxSource, v22);
		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(linkedRepoData, abxSource)); // not changedYet

		assertEquals(Maps.makeMap(abxSource, v21v22List), linkedRepoData.toAppend);

		linkedRepoData.setCommitMessage("someMessage");
		linkedRepoData.commit();
		List<Map<String, Object>> actual = IRepoReader.Utils.readAllRows(newLinkedRepo(), abxSource);
		List<Map<String, Object>> expected = Arrays.asList(v11, v12, v21, v22);
		assertEquals(expected, actual);
	}

	public void testAppendWorksOnEmptyFile() {
		linkedRepoData.append(abxSource, v11);
		linkedRepoData.append(abxSource, v12);
		linkedRepoData.setCommitMessage("someMessage");
		linkedRepoData.commit();
		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(newLinkedRepo(), abxSource));
	}

	public void testAppendAddsToLock() {
		putFile(abxFile, v11v12Json);
		linkedRepoData.append(abxSource, v21);
		assertEquals(1, linkedRepoData.locks().size());
		assertNotNull(linkedRepoData.locks().get("a/b"));
	}

	public void testAppendIgnoredWithRollback() {
		putFile(abxFile, v11v12Json);

		linkedRepoData.append(abxSource, v21);
		linkedRepoData.append(abxSource, v22);

		linkedRepoData.rollback();
		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(newLinkedRepo(), abxSource));
	}

	public void testChangeDoesntChangeBeforeCommitThenChanges() {
		putFile(abxFile, v11v12Json);
		linkedRepoData.change(abxSource, 0, v21);
		linkedRepoData.change(abxSource, 1, v22);

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(linkedRepoData, abxSource)); // not changedYet

		linkedRepoData.setCommitMessage("someMessage");
		linkedRepoData.commit();
		assertEquals(v21v22List, IRepoReader.Utils.readAllRows(newLinkedRepo(), abxSource));
	}

	public void testNeedsCommitMessageToCommit() {
		putFile(abxFile, v11v12Json);
		linkedRepoData.change(abxSource, 0, v21);
		Tests.assertThrowsWithMessage("Commit message not set", IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				linkedRepoData.commit();
			}
		});
	}

	public void testDontNeedToSetCommitMessageUnlessChangesAreMade() {
		putFile(abxFile, v11v12Json);
		linkedRepoData.readRaw(abxSource);
		linkedRepoData.commit();
	}

	public void testCannotSetCommitTwice() {
		putFile(abxFile, v11v12Json);
		linkedRepoData.change(abxSource, 0, v21);
		linkedRepoData.setCommitMessage("someMessage");
		Tests.assertThrowsWithMessage("Commit message already set\nOld: someMessage\nNew: someNewMessage", IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				linkedRepoData.setCommitMessage("someNewMessage");
			}
		});
	}

	public void testChangeAddsTolocks() {
		putFile(abxFile, v11v12Json);
		linkedRepoData.change(abxSource, 0, v21);
		assertEquals(1, linkedRepoData.locks().size());
		assertNotNull(linkedRepoData.locks().get("a/b"));

	}

	public void testChangesThrowsExceptionIfTryAndChangeTwiceInSameTransaction() {
		putFile(abxFile, v11v12Json);
		linkedRepoData.change(abxSource, 0, v21);
		Tests.assertThrowsWithMessage("Cannot make change to same item twice. " + abxSource + " 0 {c=2, v=2}. Currently this is to be changed to {c=2, v=1}", CannotChangeTwiceException.class, new Runnable() {
			@Override
			public void run() {
				linkedRepoData.change(abxSource, 0, v22);
			}
		});

	}

	public void testChangeIgnoredIfRollback() {
		putFile(abxFile, v11v12Json);
		linkedRepoData.change(abxSource, 0, v21);
		linkedRepoData.change(abxSource, 1, v22);

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(linkedRepoData, abxSource));

		linkedRepoData.rollback();
		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(newLinkedRepo(), abxSource));
	}

	public void testDeleteAddsTolocks() {
		putFile(abxFile, v11v12Json);
		linkedRepoData.delete(abxSource, 0);
		assertEquals(1, linkedRepoData.locks().size());
		assertNotNull(linkedRepoData.locks().get("a/b"));
	}

	public void testDeleteRemovesItemWhenCommitOccurs() {
		putFile(abxFile, v11v12v21v22Json);
		linkedRepoData.delete(abxSource, 1);
		linkedRepoData.delete(abxSource, 3);
		assertEquals(Arrays.asList(v11, v12, v21, v22), IRepoReader.Utils.readAllRows(linkedRepoData, abxSource)); // not changedYet
		linkedRepoData.setCommitMessage("someMessage");
		linkedRepoData.commit();
		assertEquals(Arrays.asList(v11, v21), IRepoReader.Utils.readAllRows(newLinkedRepo(), abxSource));
	}

	public void testDeleteWorksWithoutRead() {
		putFile(abxFile, v11v12v21v22Json);
		linkedRepoData.delete(abxSource, 1);
		linkedRepoData.delete(abxSource, 3);
		linkedRepoData.setCommitMessage("someMessage");
		linkedRepoData.commit();
		assertEquals(Arrays.asList(v11, v21), IRepoReader.Utils.readAllRows(newLinkedRepo(), abxSource));
	}

	public void testChangeAppendAndDeleteAllWorkTogether() {
		putFile(abxFile, v11v12v21Json);
		linkedRepoData.delete(abxSource, 1);
		linkedRepoData.change(abxSource, 2, v31);
		linkedRepoData.append(abxSource, v22);
		linkedRepoData.setCommitMessage("someMessage");
		linkedRepoData.commit();
		assertEquals(Arrays.asList(v11, v31, v22), IRepoReader.Utils.readAllRows(newLinkedRepo(), abxSource));
	}

	public void testSetProperty() {
		putFile(abxFile, Json.mapToString("a", 11l, "b", 12l) + "\n" + Json.mapToString("a", 21l, "b", 22l));
		IRepoData.Utils.setProperty(linkedRepoData, abxSource, 0, "a", "newa1");
		IRepoData.Utils.setProperty(linkedRepoData, abxSource, 1, "b", "newb2");
		linkedRepoData.setCommitMessage("someMessage");
		linkedRepoData.commit();

		List<Map<String, Object>> expected = Arrays.asList(Maps.stringObjectMap("a", "newa1", "b", 12l), Maps.stringObjectMap("a", 21l, "b", "newb2"));
		List<Map<String, Object>> actual = IRepoReader.Utils.readAllRows(newLinkedRepo(), abxSource);
		assertEquals(expected, actual);
	}

	public void testRead_Source() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v21v22Json);

		SourcedMap abx0v11 = new SourcedMap(abxSource, 0, v11);
		SourcedMap abx1v12 = new SourcedMap(abxSource, 1, v12);
		SourcedMap acx0v21 = new SourcedMap(acxSource, 0, v21);
		SourcedMap acx1v22 = new SourcedMap(acxSource, 1, v22);

		checkRead(abxSource, 0, abx0v11, abx1v12);
		checkRead(abxSource, 1, abx1v12);
		checkRead(abxSource, 2);
		checkRead(abxSource, 100);

		checkRead(acxSource, 0, acx0v21, acx1v22);
		checkRead(acxSource, 1, acx1v22);
		checkRead(acxSource, 2);
		checkRead(acxSource, 100);
	}

	public void testRead_Sources() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v21v22Json);

		SourcedMap abx0v11 = new SourcedMap(abxSource, 0, v11);
		SourcedMap abx1v12 = new SourcedMap(abxSource, 1, v12);
		SourcedMap acx0v21 = new SourcedMap(acxSource, 0, v21);
		SourcedMap acx1v22 = new SourcedMap(acxSource, 1, v22);

		checkRead(new SourcesMock(linkedRepoData, "", "", abxSource), 0, abx0v11, abx1v12);
		checkRead(new SourcesMock(linkedRepoData, "", "", abxSource), 1, abx1v12);
		checkRead(new SourcesMock(linkedRepoData, "", "", abxSource), 2);
		checkRead(new SourcesMock(linkedRepoData, "", "", abxSource), 100);

		checkRead(new SourcesMock(linkedRepoData, "", "", abxSource, acxSource), 0, abx0v11, abx1v12, acx0v21, acx1v22);
		checkRead(new SourcesMock(linkedRepoData, "", "", abxSource, acxSource), 1, abx1v12, acx0v21, acx1v22);
		checkRead(new SourcesMock(linkedRepoData, "", "", abxSource, acxSource), 2, acx0v21, acx1v22);
		checkRead(new SourcesMock(linkedRepoData, "", "", abxSource, acxSource), 3, acx1v22);
		checkRead(new SourcesMock(linkedRepoData, "", "", abxSource, acxSource), 4);
		checkRead(new SourcesMock(linkedRepoData, "", "", abxSource, acxSource), 100);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		initRepos(remoteFacard, "a/b", "a/c");
	}

}
