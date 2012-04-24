package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Arrays;
import java.util.List;

import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;

abstract public class AbstractRepoPrimTest extends RepoTest {

	protected GitFacard gitFacard;
	private final String oneAndAHalfLines = "line1\nline2";
	private final String twoLines = "line1\nline2\n";

	private final String v11Json = Json.toString(v11);
	private final String v12Json = Json.toString(v12);
	private final String v21Json = Json.toString(v21);
	private final String v22Json = Json.toString(v22);
	private final String v31Json = Json.toString(v31);
	private final String v11v12Json = v11Json + "\n" + v12Json;
	private final String v11v12v21Json = v11Json + "\n" + v12Json + "\n" + v21Json;
	private final String v11v12v21v22Json = v11Json + "\n" + v12Json + "\n" + v21Json + "\n" + v22Json;
	protected String abxFile;
	protected String acxFile;

	private final List<String> twoLinesList = Arrays.asList("line1", "line2");

	protected RepoPrim repoPrim;

	protected ISingleSource abxSource;
	protected ISingleSource acxSouce;

	abstract protected void putFile(String rl, String lines);

	public void testRead() {
		putFile(abxFile, twoLines);

		assertEquals(twoLinesList, repoPrim.read(abxSource));
		assertEquals(twoLinesList, repoPrim.read(abxSource));

		gitFacard.putFileReturningRepoRl(abxFile, oneAndAHalfLines);
		assertEquals(twoLinesList, repoPrim.read(abxSource));
	}

	public void testReadCachesResults() {
		putFile(abxFile, twoLines);
		assertSame(repoPrim.read(abxSource), repoPrim.read(abxSource));
	}

	public void testReadLockedTheRepo() {
		putFile(abxFile, twoLines);
		putFile(acxFile, twoLines);

		assertEquals(0, repoPrim.locks.size());

		assertEquals(twoLinesList, repoPrim.read(abxSource));
		assertEquals(1, repoPrim.locks.size());
		assertNotNull(repoPrim.locks.get("a/b"));

		assertEquals(twoLinesList, repoPrim.read(abxSource));
		assertEquals(1, repoPrim.locks.size());
		assertNotNull(repoPrim.locks.get("a/b"));

		assertEquals(twoLinesList, repoPrim.read(abxSource));
		assertEquals(1, repoPrim.locks.size());
		assertNotNull(repoPrim.locks.get("a/b"));

		assertEquals(twoLinesList, repoPrim.read(acxSouce));
		assertEquals(2, repoPrim.locks.size());
		assertNotNull(repoPrim.locks.get("a/b"));
		assertNotNull(repoPrim.locks.get("a/c"));
	}

	public void testCommitReleasesLocks() {
		putFile(abxFile, twoLines);
		putFile(acxFile, twoLines);

		assertEquals(twoLinesList, repoPrim.read(abxSource));
		assertEquals(twoLinesList, repoPrim.read(acxSouce));

		repoPrim.commit();
		assertEquals(0, repoPrim.locks.size());
	}

	public void testRollbackReleasesLocks() {
		putFile(abxFile, twoLines);
		putFile(acxFile, twoLines);

		assertEquals(twoLinesList, repoPrim.read(abxSource));
		assertEquals(twoLinesList, repoPrim.read(acxSouce));

		repoPrim.rollback();
		assertEquals(0, repoPrim.locks.size());
	}

	public void testAppendDoesntChangeBeforeCommitButAddsToAppendList() {
		putFile(abxFile, v11v12Json);
		repoPrim.append(abxSource, v21);
		repoPrim.append(abxSource, v22);
		assertEquals(Arrays.asList(v11Json, v12Json), repoPrim.read(abxSource)); // not changedYet

		assertEquals(Maps.makeMap(abxSource, Arrays.asList(v21, v22)), repoPrim.toAppend);

		repoPrim.commit();
		assertEquals(Arrays.asList(v11Json, v12Json, v21Json, v22Json), newRepoPrim().read(abxSource));
	}

	public void testAppendWorksOnEmptyFile() {
		repoPrim.append(abxSource, v11);
		repoPrim.append(abxSource, v12);
		repoPrim.commit();
		assertEquals(Arrays.asList(v11Json, v12Json), newRepoPrim().read(abxSource));
	}

	public void testAppendAddsToLock() {
		putFile(abxFile, v11v12Json);
		repoPrim.append(abxSource, v21);
		assertEquals(1, repoPrim.locks.size());
		assertNotNull(repoPrim.locks.get("a/b"));
	}

	public void testAppendIgnoredWithRollback() {
		putFile(abxFile, v11v12Json);

		repoPrim.append(abxSource, v21);
		repoPrim.append(abxSource, v22);

		repoPrim.rollback();
		assertEquals(Arrays.asList(v11Json, v12Json), newRepoPrim().read(abxSource));
	}

	public void testChangeDoesntChangeBeforeCommitThenChanges() {
		putFile(abxFile, v11v12Json);
		repoPrim.change(abxSource, 0, v21);
		repoPrim.change(abxSource, 1, v22);

		assertEquals(Arrays.asList(v11Json, v12Json), repoPrim.read(abxSource)); // not changedYet

		repoPrim.commit();
		assertEquals(Arrays.asList(v21Json, v22Json), newRepoPrim().read(abxSource));
	}

	public void testChangeAddsToLocks() {
		putFile(abxFile, v11v12Json);
		repoPrim.change(abxSource, 0, v21);
		assertEquals(1, repoPrim.locks.size());
		assertNotNull(repoPrim.locks.get("a/b"));

	}

	public void testChangeIgnoredIfRollback() {
		putFile(abxFile, v11v12Json);
		repoPrim.change(abxSource, 0, v21);
		repoPrim.change(abxSource, 1, v22);

		assertEquals(Arrays.asList(v11Json, v12Json), repoPrim.read(abxSource));

		repoPrim.rollback();
		assertEquals(Arrays.asList(v11Json, v12Json), newRepoPrim().read(abxSource));
	}

	public void testDeleteAddsToLocks() {
		putFile(abxFile, v11v12Json);
		repoPrim.delete(abxSource, 0);
		assertEquals(1, repoPrim.locks.size());
		assertNotNull(repoPrim.locks.get("a/b"));
	}

	public void testDeleteRemovesItemWhenCommitOccurs() {
		putFile(abxFile, v11v12v21v22Json);
		repoPrim.delete(abxSource, 1);
		repoPrim.delete(abxSource, 3);
		assertEquals(Arrays.asList(v11Json, v12Json, v21Json, v22Json), repoPrim.read(abxSource)); // not changedYet
		repoPrim.commit();
		assertEquals(Arrays.asList(v11Json, v21Json), newRepoPrim().read(abxSource));
	}

	private RepoPrim newRepoPrim() {
		return repoPrim = new RepoPrim(gitFacard, "someOtherCommitMessage");
	}

	public void testDeleteWorksWithoutRead() {
		putFile(abxFile, v11v12v21v22Json);
		repoPrim.delete(abxSource, 1);
		repoPrim.delete(abxSource, 3);
		repoPrim.commit();
		assertEquals(Arrays.asList(v11Json, v21Json), newRepoPrim().read(abxSource));
	}

	public void testChangeAppendAndDeleteAllWorkTogether() {
		putFile(abxFile, v11v12v21Json);
		repoPrim.delete(abxSource, 1);
		repoPrim.change(abxSource, 2, v31);
		repoPrim.append(abxSource, v22);
		repoPrim.commit();
		assertEquals(Arrays.asList(v11Json, v31Json, v22Json), newRepoPrim().read(abxSource));

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gitFacard = new GitFacard(remoteRoot);
		gitFacard.init("a/b");
		gitFacard.init("a/c");
		repoPrim = new RepoPrim(gitFacard, "someCommitMessage");
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			repoPrim.rollback();
		} finally {
			super.tearDown();
		}
	}

}
