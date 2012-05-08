package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.newGit.IRepoReader;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;

@SuppressWarnings("unchecked")
public class LocalAndRemoteRepoTest extends RepoTest {

	private final static String repo1 = "one";
	private final static String file1_1 = repo1 + "/file/data.txt";

	private final static ISingleSource source1_1 = new RawSingleSource(file1_1);

	public void testReadRawPullsFromRemoteWhenNeedsToClone() {
		initRepos(remoteFacard, repo1);
		putFile(remoteFacard, file1_1, null, v11, v12);
		addAllAndCommit(remoteFacard, repo1);

		List<Map<String, Object>> actual = IRepoReader.Utils.readAllRows(linkedRepoData, source1_1);
		assertEquals(Arrays.asList(v11, v12), actual);
	}

	public void testPullsFromRemoteIfHasntPulled() {
		initRepos(remoteFacard, repo1);
		putFile(remoteFacard, file1_1, null, v11, v12);
		addAllAndCommit(remoteFacard, repo1);
		IGitFacard.Utils.clone(linkedFacard, repo1);

		putFile(remoteFacard, file1_1, null, v21, v22);
		addAllAndCommit(remoteFacard, repo1);

		List<Map<String, Object>> actual = IRepoReader.Utils.readAllRows(linkedRepoData, source1_1);
		assertEquals(Arrays.asList(v21, v22), actual);
	}

	public void testDoesntPullIfHasPulledAlready() {
		initRepos(remoteFacard, repo1);
		putFile(remoteFacard, file1_1, null, v11, v12);
		addAllAndCommit(remoteFacard, repo1);
		IGitFacard.Utils.clone(linkedFacard, repo1);
		putFile(remoteFacard, file1_1, null, v21, v22);
		addAllAndCommit(remoteFacard, repo1);

		hasPulled.add(repo1);
		List<Map<String, Object>> actual = IRepoReader.Utils.readAllRows(linkedRepoData, source1_1);
		assertEquals(Arrays.asList(v11, v12), actual);
	}

}
