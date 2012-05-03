package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.List;
import java.util.Set;

import org.softwareFm.crowdsource.api.internal.Container;
import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback.EnsureUniqueParameter;
import org.softwareFm.crowdsource.utilities.collections.ITransactionalMutableSimpleSet;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;

public class RepoDataConfigureLocalContainerTest extends RepoTest {
	private Container container;

	public void testContainerMakesNew_RepoData_LinkedRepoReader_HasPulled_WithEachTransaction() {
		EnsureUniqueParameter<IRepoData> containerMemory = ICallback.Utils.<IRepoData> ensureUniqueParameter();
		List<ITransaction<?>> transactions = Lists.newList();
		for (int i = 0; i < 10; i++)
			transactions.add(container.access(IRepoData.class, containerMemory));
		for (ITransaction<?> transaction : transactions)
			transaction.get();

		Set<LinkedRepoReader> readers = Sets.newSet();
		Set<ITransactionalMutableSimpleSet<String>> hasPulled = Sets.newSet();
		for (IRepoData data : containerMemory.list) {
			RepoData repoData = (RepoData) data;
			LinkedRepoReader linkedRepoReader = (LinkedRepoReader) repoData.repoReader;
			readers.add(linkedRepoReader);
			hasPulled.add(linkedRepoReader.hasPulled);
		}
		assertEquals(containerMemory.list.size(), readers.size());
		assertEquals(containerMemory.list.size(), hasPulled.size());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		container = new Container(transactionManager, null);
		IRepoData.Utils.configureLocalContainer(container, localFacard, repoLocator, getAccessControl());
	}
}
