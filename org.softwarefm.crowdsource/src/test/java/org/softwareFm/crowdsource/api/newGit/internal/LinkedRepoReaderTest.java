package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Map;

import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.utilities.json.Json;

public class LinkedRepoReaderTest extends RepoTest {

	private final RawSingleSource oneA = new RawSingleSource("one/a");
	private final RawSingleSource twoB = new RawSingleSource("two/b");

	public void testReadRawPullsIfRepoRlNotInHasPulled() {
		initRepos(remoteFacard, "one", "two");
		putData(remoteFacard, v11, v12);
		checkReadRaw(v11, v12);
		checkReadRaw(v11, v12);
	}

	public void testReadRawDoesntPullIfRepoRlIsInHasPulled() {
		initRepos(remoteFacard, "one", "two");

		putData(remoteFacard, v11, v12);
		checkReadRaw(v11, v12);

		putData(remoteFacard, v21, v22);

		checkReadRaw(v11, v12);
	}

	public void testReadRawUpdatesHasPulledIfNeeded() {
		initRepos(remoteFacard, "one", "two");
		putData(remoteFacard, v11, v12);

		assertFalse(hasPulled.contains("one"));
		assertFalse(hasPulled.contains("two"));

		checkReadRaw(v11, v12);

		assertTrue(hasPulled.contains("one"));
		assertTrue(hasPulled.contains("two"));
	}


	private void checkReadRaw(Map<String, Object> one, Map<String, Object> two) {
		checkReadRaw(one, two, false);
	}

	@SuppressWarnings("unchecked")
	private void checkReadRaw(Map<String, Object> one, Map<String, Object> two, boolean commit) {
		SimpleRepoReader delegate = new SimpleRepoReader(localFacard);
		LinkedRepoReader repoReader = new LinkedRepoReader(delegate, localFacard, repoLocator, hasPulled);
		try {

			assertEquals(Json.asList(one), repoReader.readRaw(oneA));
			assertEquals(Json.asList(two), repoReader.readRaw(twoB));
		} finally {
			if (commit)
				repoReader.commit();
			else
				repoReader.rollback();
		}
	}

	@SuppressWarnings("unchecked")
	private void putData(IGitFacard facard, Map<String, Object> one, Map<String, Object> two) {
		putFile(remoteFacard, "one/a", null, one);
		putFile(remoteFacard, "two/b", null, two);
		addAllAndCommit(remoteFacard, "one", "two");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

}
