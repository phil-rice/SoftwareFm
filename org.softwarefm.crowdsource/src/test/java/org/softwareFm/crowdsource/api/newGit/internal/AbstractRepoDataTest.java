package org.softwareFm.crowdsource.api.newGit.internal;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.RepoLocation;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;

abstract public class AbstractRepoDataTest extends RepoTest {

	private final String v11Json = Json.toString(v11);
	private final String v12Json = Json.toString(v12);
	private final String v21Json = Json.toString(v21);
	private final String v22Json = Json.toString(v22);
	private final String v31Json = Json.toString(v31);
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

	abstract protected void putFile(String rl, String lines);

	public void testSetup(){
		assertNotNull(abxSource);
		assertNotNull(abySource);
		assertNotNull(acxSource);
		assertNotNull(acySource);
	}
	
	public void testReadAllRows() {
		putFile(abxFile, v11v12Json);

		assertEquals(v11v12List, repoData.readAllRows(abxSource));
		assertEquals(v11v12List, repoData.readAllRows(abxSource));
	}

	public void testCountLines() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v11v12v21v22Json);
		assertEquals(2, repoData.countLines(abxSource));
		assertEquals(4, repoData.countLines(acxSource));
	}

	public void testCountLinesSources() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v11v12v21v22Json);
		assertEquals(Maps.makeMap(), repoData.countLines(new SourcesMock(repoData, "", "")));
		assertEquals(Maps.makeMap(abxSource, 2), repoData.countLines(new SourcesMock(repoData, "", "", abxSource)));
		assertEquals(Maps.makeMap(abxSource, 2, acxSource, 4), repoData.countLines(new SourcesMock(repoData, "", "", abxSource, acxSource)));
	}

	public void testCountLinesWithNoDataReturns0() {
		assertEquals(0, repoData.countLines(abxSource));
		assertEquals(0, repoData.countLines(acxSource));
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

		List<Map<String, Object>> first = repoData.readAllRows(abxSource);
		List<Map<String, Object>> second = repoData.readAllRows(abxSource);
		assertEquals(first.size(), second.size());
		for (int i = 0; i < first.size(); i++)
			assertSame(first.get(i), second.get(i));
	}

	public void testReadAllRowsAndReadRowsAreSameValue() {
		putFile(abxFile, v11v12Json);

		List<Map<String, Object>> all = repoData.readAllRows(abxSource);
		for (int i = 0; i < all.size(); i++)
			assertSame(all.get(i), repoData.readRow(abxSource, i));
	}

	public void testReadFirstRow() {
		putFile(abxFile, v11v12Json);

		List<Map<String, Object>> all = repoData.readAllRows(abxSource);
		assertSame(all.get(0), repoData.readFirstRow(abxSource));
		assertEquals(Maps.emptyStringObjectMap(), repoData.readFirstRow(acxSource));

	}

	public void testReadLockedTheRepo() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v21v22Json);

		assertEquals(0, repoData.locks.size());

		assertEquals(v11v12List, repoData.readAllRows(abxSource));
		assertEquals(1, repoData.locks.size());
		assertNotNull(repoData.locks.get("a/b"));

		assertEquals(v11v12List, repoData.readAllRows(abxSource));
		assertEquals(1, repoData.locks.size());
		assertNotNull(repoData.locks.get("a/b"));

		assertEquals(v11v12List, repoData.readAllRows(abxSource));
		assertEquals(1, repoData.locks.size());
		assertNotNull(repoData.locks.get("a/b"));

		assertEquals(v21v22List, repoData.readAllRows(acxSource));
		assertEquals(2, repoData.locks.size());
		assertNotNull(repoData.locks.get("a/b"));
		assertNotNull(repoData.locks.get("a/c"));
	}

	public void testCommitReleasesLocks() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v21v22Json);

		assertEquals(v11v12List, repoData.readAllRows(abxSource));
		assertEquals(v21v22List, repoData.readAllRows(acxSource));

		repoData.commit();
		assertEquals(0, repoData.locks.size());
	}

	public void testRollbackReleasesLocks() {
		putFile(abxFile, v11v12Json);
		putFile(acxFile, v21v22Json);

		assertEquals(v11v12List, repoData.readAllRows(abxSource));
		assertEquals(v21v22List, repoData.readAllRows(acxSource));
		repoData.rollback();
		assertEquals(0, repoData.locks.size());
	}

	public void testAppendDoesntChangeBeforeCommitButAddsToAppendList() {
		putFile(abxFile, v11v12Json);
		repoData.append(abxSource, v21);
		repoData.append(abxSource, v22);
		assertEquals(v11v12List, repoData.readAllRows(abxSource)); // not changedYet

		assertEquals(Maps.makeMap(abxSource, v21v22List), repoData.toAppend);

		repoData.commit();
		List<Map<String, Object>> actual = newRepoPrim().readAllRows(abxSource);
		List<Map<String, Object>> expected = Arrays.asList(v11, v12, v21, v22);
		assertEquals(expected, actual);
	}

	public void testAppendWorksOnEmptyFile() {
		repoData.append(abxSource, v11);
		repoData.append(abxSource, v12);
		repoData.commit();
		assertEquals(v11v12List, newRepoPrim().readAllRows(abxSource));
	}

	public void testAppendAddsToLock() {
		putFile(abxFile, v11v12Json);
		repoData.append(abxSource, v21);
		assertEquals(1, repoData.locks.size());
		assertNotNull(repoData.locks.get("a/b"));
	}

	public void testAppendIgnoredWithRollback() {
		putFile(abxFile, v11v12Json);

		repoData.append(abxSource, v21);
		repoData.append(abxSource, v22);

		repoData.rollback();
		assertEquals(v11v12List, newRepoPrim().readAllRows(abxSource));
	}

	public void testChangeDoesntChangeBeforeCommitThenChanges() {
		putFile(abxFile, v11v12Json);
		repoData.change(abxSource, 0, v21);
		repoData.change(abxSource, 1, v22);

		assertEquals(v11v12List, repoData.readAllRows(abxSource)); // not changedYet

		repoData.commit();
		assertEquals(v21v22List, newRepoPrim().readAllRows(abxSource));
	}

	public void testChangeAddsToLocks() {
		putFile(abxFile, v11v12Json);
		repoData.change(abxSource, 0, v21);
		assertEquals(1, repoData.locks.size());
		assertNotNull(repoData.locks.get("a/b"));

	}

	public void testChangeIgnoredIfRollback() {
		putFile(abxFile, v11v12Json);
		repoData.change(abxSource, 0, v21);
		repoData.change(abxSource, 1, v22);

		assertEquals(v11v12List, repoData.readAllRows(abxSource));

		repoData.rollback();
		assertEquals(v11v12List, newRepoPrim().readAllRows(abxSource));
	}

	public void testDeleteAddsToLocks() {
		putFile(abxFile, v11v12Json);
		repoData.delete(abxSource, 0);
		assertEquals(1, repoData.locks.size());
		assertNotNull(repoData.locks.get("a/b"));
	}

	public void testDeleteRemovesItemWhenCommitOccurs() {
		putFile(abxFile, v11v12v21v22Json);
		repoData.delete(abxSource, 1);
		repoData.delete(abxSource, 3);
		assertEquals(Arrays.asList(v11, v12, v21, v22), repoData.readAllRows(abxSource)); // not changedYet
		repoData.commit();
		assertEquals(Arrays.asList(v11, v21), newRepoPrim().readAllRows(abxSource));
	}

	private RepoData newRepoPrim() {
		return repoData = new RepoData(gitFacard, "someOtherCommitMessage");
	}

	public void testDeleteWorksWithoutRead() {
		putFile(abxFile, v11v12v21v22Json);
		repoData.delete(abxSource, 1);
		repoData.delete(abxSource, 3);
		repoData.commit();
		assertEquals(Arrays.asList(v11, v21), newRepoPrim().readAllRows(abxSource));
	}

	public void testChangeAppendAndDeleteAllWorkTogether() {
		putFile(abxFile, v11v12v21Json);
		repoData.delete(abxSource, 1);
		repoData.change(abxSource, 2, v31);
		repoData.append(abxSource, v22);
		repoData.commit();
		assertEquals(Arrays.asList(v11, v31, v22), newRepoPrim().readAllRows(abxSource));
	}

	public void testFindRepositories() {
		RepoLocation abrepo = new RepoLocation(new File(remoteRoot, "a/b"), "a/b");
		RepoLocation acrepo = new RepoLocation(new File(remoteRoot, "a/c"), "a/c");
		
		assertEquals(Sets.makeSet(), repoData.findRepositories(new SourcesMock(repoData, "", "")));
		assertEquals(Sets.makeSet(abrepo), repoData.findRepositories(new SourcesMock(repoData, "", "", abxSource)));
		assertEquals(Sets.makeSet(abrepo), repoData.findRepositories(new SourcesMock(repoData, "", "", abxSource, abySource)));
		assertEquals(Sets.makeSet(abrepo, acrepo), repoData.findRepositories(new SourcesMock(repoData, "", "", abxSource, acxSource)));
		assertEquals(Sets.makeSet(abrepo, acrepo), repoData.findRepositories(new SourcesMock(repoData, "", "", abxSource, abySource, abySource, acySource)));
	}
	
	public void testFindRepository(){
		RepoLocation abrepo = new RepoLocation(new File(remoteRoot, "a/b"), "a/b");
		RepoLocation acrepo = new RepoLocation(new File(remoteRoot, "a/c"), "a/c");
		
		assertEquals(abrepo, repoData.findRepository(abxSource));
		assertEquals(abrepo, repoData.findRepository(abySource));
		assertEquals(acrepo, repoData.findRepository(acxSource));
		assertEquals(acrepo, repoData.findRepository(acySource));
		
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gitFacard.init("a/b");
		gitFacard.init("a/c");

	}

	@Override
	protected void tearDown() throws Exception {
		try {
			repoData.rollback();
		} finally {
			super.tearDown();
		}
	}

}
