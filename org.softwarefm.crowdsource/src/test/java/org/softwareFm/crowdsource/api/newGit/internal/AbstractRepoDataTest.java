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

	private final String v11Json = Json.toString(v11);
	private final String v12Json = Json.toString(v12);
	private final String v21Json = Json.toString(v21);
	private final String v22Json = Json.toString(v22);
	private final String v11v12Json = v11Json + "\n" + v12Json;
	private final String v21v22Json = v21Json + "\n" + v22Json;
	private final String v11v12v21Json = v11Json + "\n" + v12Json + "\n" + v21Json;
	private final String v11v12v21v22Json = v11Json + "\n" + v12Json + "\n" + v21Json + "\n" + v22Json;
	protected final String abxFile = "a/b/x/data.txt";
	protected final String abyFile = "a/b/y/data.txt";
	protected final String acxFile = "a/c/x/data.txt";
	protected final String acyFile = "a/c/y/data.txt";

	protected ISingleSource abxSource;
	protected ISingleSource abySource;
	protected ISingleSource acxSource;
	protected ISingleSource acySource;

	private final List<Map<String, Object>> v11v12List = Arrays.asList(v11, v12);
	private final List<Map<String, Object>> v21v22List = Arrays.asList(v21, v22);

	abstract protected void putFilePrim(String rl, String lines);

	protected void putFile(String rl, String lines) {
		cloneIfNeeded();
		putFilePrim(rl, lines);
	}

	private void cloneIfNeeded() {
		if (!haveCloned) {
			IGitFacard.Utils.clone(localFacard, "a/b");
			IGitFacard.Utils.clone(localFacard, "a/c");
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

		assertEquals(abRemote, repoData.findRepository(abxSource));
		assertEquals(abRemote, repoData.findRepository(abySource));
		assertEquals(acRemote, repoData.findRepository(acxSource));
		assertEquals(acRemote, repoData.findRepository(acySource));
	}

	public void testFindRepositoryReturnsTheLocalLocationIfLocalExists() {
		cloneIfNeeded();

		RepoLocation abLocal = RepoLocation.local(localRoot, "a/b");
		RepoLocation acLocal = RepoLocation.local(localRoot, "a/c");

		assertEquals(abLocal, repoData.findRepository(abxSource));
		assertEquals(abLocal, repoData.findRepository(abySource));
		assertEquals(acLocal, repoData.findRepository(acxSource));
		assertEquals(acLocal, repoData.findRepository(acySource));
	}

	public void testFindRepositories() {
		IGitFacard.Utils.clone(localFacard, "a/b");

		RepoLocation abrepo = RepoLocation.local(localRoot, "a/b");
		RepoLocation acrepo = RepoLocation.remote(localRoot, "a/c");

		assertEquals(Sets.makeSet(), IRepoData.Utils.findRepositories(repoData, new SourcesMock(repoData, "", "")));
		assertEquals(Sets.makeSet(abrepo), IRepoData.Utils.findRepositories(repoData, new SourcesMock(repoData, "", "", abxSource)));
		assertEquals(Sets.makeSet(abrepo), IRepoData.Utils.findRepositories(repoData, new SourcesMock(repoData, "", "", abxSource, abySource)));
		assertEquals(Sets.makeSet(abrepo, acrepo), IRepoData.Utils.findRepositories(repoData, new SourcesMock(repoData, "", "", abxSource, acxSource)));
		assertEquals(Sets.makeSet(abrepo, acrepo), IRepoData.Utils.findRepositories(repoData, new SourcesMock(repoData, "", "", abxSource, abySource, abySource, acySource)));
	}

	public void testReadAllRows() {
		putFile(abxFile, v11v12Json);

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(repoData, abxSource));
		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(repoData, abxSource));
	}

	public void testCountLines() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v11v12v21v22Json);
		assertEquals(2, IRepoReader.Utils.countLines(repoData, abxSource));
		assertEquals(4, IRepoReader.Utils.countLines(repoData, acxSource));
	}

	public void testCountLinesSources() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v11v12v21v22Json);
		assertEquals(Maps.makeMap(), IRepoReader.Utils.countLines(repoData, new SourcesMock(repoData, "", "")));
		assertEquals(Maps.makeMap(abxSource, 2), IRepoReader.Utils.countLines(repoData, new SourcesMock(repoData, "", "", abxSource)));
		assertEquals(Maps.makeMap(abxSource, 2, acxSource, 4), IRepoReader.Utils.countLines(repoData, new SourcesMock(repoData, "", "", abxSource, acxSource)));
	}

	public void testCountLinesWithNoDataReturns0() {
		assertEquals(0, IRepoReader.Utils.countLines(repoData, abxSource));
		assertEquals(0, IRepoReader.Utils.countLines(repoData, acxSource));
	}

	public void testReadRawCachesResults() {
		putFile(abxFile, v11v12Json);

		List<String> first = repoData.readRaw(abxSource);
		List<String> second = repoData.readRaw(abxSource);
		assertEquals(first.size(), second.size());
		for (int i = 0; i < first.size(); i++)
			assertSame(first.get(i), second.get(i));
	}

	public void testReadAllRowsCachesResults() {
		putFile(abxFile, v11v12Json);

		List<Map<String, Object>> first = IRepoReader.Utils.readAllRows(repoData, abxSource);
		List<Map<String, Object>> second = IRepoReader.Utils.readAllRows(repoData, abxSource);
		assertEquals(first.size(), second.size());
		for (int i = 0; i < first.size(); i++)
			assertSame(first.get(i), second.get(i));
	}

	public void testReadAllRowsAndReadRowsAreSameValue() {
		putFile(abxFile, v11v12Json);

		List<Map<String, Object>> all = IRepoReader.Utils.readAllRows(repoData, abxSource);
		for (int i = 0; i < all.size(); i++)
			assertSame(all.get(i), repoData.readRow(abxSource, i));
	}

	public void testReadFirstRow() {
		putFile(abxFile, v11v12Json);

		List<Map<String, Object>> all = IRepoReader.Utils.readAllRows(repoData, abxSource);
		assertSame(all.get(0), IRepoReader.Utils.readFirstRow(repoData, abxSource));
		assertEquals(Maps.emptyStringObjectMap(), IRepoReader.Utils.readFirstRow(repoData, acxSource));

	}

	public void testReadLockedTheRepo() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v21v22Json);

		assertEquals(0, repoData.locks().size());

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(repoData, abxSource));
		assertEquals(1, repoData.locks().size());
		assertNotNull(repoData.locks().get("a/b"));

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(repoData, abxSource));
		assertEquals(1, repoData.locks().size());
		assertNotNull(repoData.locks().get("a/b"));

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(repoData, abxSource));
		assertEquals(1, repoData.locks().size());
		assertNotNull(repoData.locks().get("a/b"));

		assertEquals(v21v22List, IRepoReader.Utils.readAllRows(repoData, acxSource));
		assertEquals(2, repoData.locks().size());
		assertNotNull(repoData.locks().get("a/b"));
		assertNotNull(repoData.locks().get("a/c"));
	}

	public void testCommitReleaseslocks() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v21v22Json);

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(repoData, abxSource));
		assertEquals(v21v22List, IRepoReader.Utils.readAllRows(repoData, acxSource));

		repoData.setCommitMessage("someMessage");
		repoData.commit();
		assertEquals(0, repoData.locks().size());
	}

	public void testRollbackReleaseslocks() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v21v22Json);

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(repoData, abxSource));
		assertEquals(v21v22List, IRepoReader.Utils.readAllRows(repoData, acxSource));
		repoData.rollback();
		assertEquals(0, repoData.locks().size());
	}

	public void testCommitCausesHasPulledToBeCommitted() {
		putFile(abxFile, v11v12Json);
		assertEquals(0, hasPulledCommitCount.get());
		repoData.commit();
		assertEquals(1, hasPulledCommitCount.get());
		assertEquals(0, hasPulledRollbackCount.get());
	}

	public void testRollbackCausesHasPulledToBeRollbacked() {
		putFile(abxFile, v11v12Json);
		assertEquals(0, hasPulledRollbackCount.get());
		repoData.rollback();
		assertEquals(0, hasPulledCommitCount.get());
		assertEquals(1, hasPulledRollbackCount.get());
	}

	public void testAppendDoesntChangeBeforeCommitButAddsToAppendList() {
		putFile(abxFile, v11v12Json);
		repoData.append(abxSource, v21);
		repoData.append(abxSource, v22);
		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(repoData, abxSource)); // not changedYet

		assertEquals(Maps.makeMap(abxSource, v21v22List), repoData.toAppend);

		repoData.setCommitMessage("someMessage");
		repoData.commit();
		List<Map<String, Object>> actual = IRepoReader.Utils.readAllRows(newRepo(), abxSource);
		List<Map<String, Object>> expected = Arrays.asList(v11, v12, v21, v22);
		assertEquals(expected, actual);
	}

	public void testAppendWorksOnEmptyFile() {
		repoData.append(abxSource, v11);
		repoData.append(abxSource, v12);
		repoData.setCommitMessage("someMessage");
		repoData.commit();
		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(newRepo(), abxSource));
	}

	public void testAppendAddsToLock() {
		putFile(abxFile, v11v12Json);
		repoData.append(abxSource, v21);
		assertEquals(1, repoData.locks().size());
		assertNotNull(repoData.locks().get("a/b"));
	}

	public void testAppendIgnoredWithRollback() {
		putFile(abxFile, v11v12Json);

		repoData.append(abxSource, v21);
		repoData.append(abxSource, v22);

		repoData.rollback();
		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(newRepo(), abxSource));
	}

	public void testChangeDoesntChangeBeforeCommitThenChanges() {
		putFile(abxFile, v11v12Json);
		repoData.change(abxSource, 0, v21);
		repoData.change(abxSource, 1, v22);

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(repoData, abxSource)); // not changedYet

		repoData.setCommitMessage("someMessage");
		repoData.commit();
		assertEquals(v21v22List, IRepoReader.Utils.readAllRows(newRepo(), abxSource));
	}

	public void testNeedsCommitMessageToCommit() {
		putFile(abxFile, v11v12Json);
		repoData.change(abxSource, 0, v21);
		Tests.assertThrowsWithMessage("Commit message not set", IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				repoData.commit();
			}
		});
	}

	public void testDontNeedToSetCommitMessageUnlessChangesAreMade() {
		putFile(abxFile, v11v12Json);
		repoData.readRaw(abxSource);
		repoData.commit();
	}

	public void testCannotSetCommitTwice() {
		putFile(abxFile, v11v12Json);
		repoData.change(abxSource, 0, v21);
		repoData.setCommitMessage("someMessage");
		Tests.assertThrowsWithMessage("Commit message already set\nOld: someMessage\nNew: someNewMessage", IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				repoData.setCommitMessage("someNewMessage");
			}
		});
	}

	public void testChangeAddsTolocks() {
		putFile(abxFile, v11v12Json);
		repoData.change(abxSource, 0, v21);
		assertEquals(1, repoData.locks().size());
		assertNotNull(repoData.locks().get("a/b"));

	}

	public void testChangesThrowsExceptionIfTryAndChangeTwiceInSameTransaction() {
		putFile(abxFile, v11v12Json);
		repoData.change(abxSource, 0, v21);
		Tests.assertThrowsWithMessage("Cannot make change to same item twice. " + abxSource + " 0 {c=2, v=2}. Currently this is to be changed to {c=2, v=1}", CannotChangeTwiceException.class, new Runnable() {
			@Override
			public void run() {
				repoData.change(abxSource, 0, v22);
			}
		});

	}

	public void testChangeIgnoredIfRollback() {
		putFile(abxFile, v11v12Json);
		repoData.change(abxSource, 0, v21);
		repoData.change(abxSource, 1, v22);

		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(repoData, abxSource));

		repoData.rollback();
		assertEquals(v11v12List, IRepoReader.Utils.readAllRows(newRepo(), abxSource));
	}

	public void testDeleteAddsTolocks() {
		putFile(abxFile, v11v12Json);
		repoData.delete(abxSource, 0);
		assertEquals(1, repoData.locks().size());
		assertNotNull(repoData.locks().get("a/b"));
	}

	public void testDeleteRemovesItemWhenCommitOccurs() {
		putFile(abxFile, v11v12v21v22Json);
		repoData.delete(abxSource, 1);
		repoData.delete(abxSource, 3);
		assertEquals(Arrays.asList(v11, v12, v21, v22), IRepoReader.Utils.readAllRows(repoData, abxSource)); // not changedYet
		repoData.setCommitMessage("someMessage");
		repoData.commit();
		assertEquals(Arrays.asList(v11, v21), IRepoReader.Utils.readAllRows(newRepo(), abxSource));
	}

	public void testDeleteWorksWithoutRead() {
		putFile(abxFile, v11v12v21v22Json);
		repoData.delete(abxSource, 1);
		repoData.delete(abxSource, 3);
		repoData.setCommitMessage("someMessage");
		repoData.commit();
		assertEquals(Arrays.asList(v11, v21), IRepoReader.Utils.readAllRows(newRepo(), abxSource));
	}

	public void testChangeAppendAndDeleteAllWorkTogether() {
		putFile(abxFile, v11v12v21Json);
		repoData.delete(abxSource, 1);
		repoData.change(abxSource, 2, v31);
		repoData.append(abxSource, v22);
		repoData.setCommitMessage("someMessage");
		repoData.commit();
		assertEquals(Arrays.asList(v11, v31, v22), IRepoReader.Utils.readAllRows(newRepo(), abxSource));
	}

	public void testSetProperty() {
		putFile(abxFile, Json.mapToString("a", 11l, "b", 12l) + "\n" + Json.mapToString("a", 21l, "b", 22l));
		IRepoData.Utils.setProperty(repoData, abxSource, 0, "a", "newa1");
		IRepoData.Utils.setProperty(repoData, abxSource, 1, "b", "newb2");
		repoData.setCommitMessage("someMessage");
		repoData.commit();

		List<Map<String, Object>> expected = Arrays.asList(Maps.stringObjectMap("a", "newa1", "b", 12l), Maps.stringObjectMap("a", 21l, "b", "newb2"));
		List<Map<String, Object>> actual = IRepoReader.Utils.readAllRows(newRepo(), abxSource);
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

		checkRead(new SourcesMock(repoData, "", "", abxSource), 0, abx0v11, abx1v12);
		checkRead(new SourcesMock(repoData, "", "", abxSource), 1, abx1v12);
		checkRead(new SourcesMock(repoData, "", "", abxSource), 2);
		checkRead(new SourcesMock(repoData, "", "", abxSource), 100);

		checkRead(new SourcesMock(repoData, "", "", abxSource, acxSource), 0, abx0v11, abx1v12, acx0v21, acx1v22);
		checkRead(new SourcesMock(repoData, "", "", abxSource, acxSource), 1, abx1v12, acx0v21, acx1v22);
		checkRead(new SourcesMock(repoData, "", "", abxSource, acxSource), 2, acx0v21, acx1v22);
		checkRead(new SourcesMock(repoData, "", "", abxSource, acxSource), 3, acx1v22);
		checkRead(new SourcesMock(repoData, "", "", abxSource, acxSource), 4);
		checkRead(new SourcesMock(repoData, "", "", abxSource, acxSource), 100);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		initRepos(remoteFacard, "a/b", "a/c");
		hasPulledCommitCount.set(0);
		hasPulledRollbackCount.set(0);

	}

}
