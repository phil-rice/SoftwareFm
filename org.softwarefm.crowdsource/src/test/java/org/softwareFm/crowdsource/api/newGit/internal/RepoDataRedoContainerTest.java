package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.softwareFm.crowdsource.api.internal.Container;
import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.IRepoDataFactory;
import org.softwareFm.crowdsource.api.newGit.IRepoReaderImplementor;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback.EnsureUniqueParameter;
import org.softwareFm.crowdsource.utilities.collections.ITransactionalMutableSimpleSet;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.tests.Tests;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.transaction.RedoTransactionException;

public class RepoDataRedoContainerTest extends RepoTest {

	private Container container;

	@SuppressWarnings("unchecked")
	public void testIfTryToReadSameFileInTwoTransactionsGetRedoTransactionExceptionInSecond() throws Exception {
		container.register(IRepoData.class, IRepoDataFactory.Utils.simpleFactory(linkedFacard));
		
		initRepos(linkedFacard, "a");
		putFile(linkedFacard, "a/b", null, v11, v12);
		addAllAndCommit(linkedFacard, "a");

		final CountDownLatch transaction1CanRead = new CountDownLatch(1);
		final CountDownLatch transaction1HasRead = new CountDownLatch(1);
		final CountDownLatch transaction1CanFinish = new CountDownLatch(1);
		final CountDownLatch transaction2Hold = new CountDownLatch(1);
		final CountDownLatch transactionsStarted = new CountDownLatch(2);
		ITransaction<Void> t1 = container.access(IRepoData.class, new ICallback<IRepoData>() {
			@Override
			public void process(IRepoData t) throws Exception {
				transactionsStarted.countDown();

				waitFor(transaction1CanRead);
				// System.out.println("About to read raw in t1. RepoData is " + t);
				t.readRaw(new RawSingleSource("a/b"));
				transaction1HasRead.countDown();

				waitFor(transaction1CanFinish);
			}

		});
		final ITransaction<Void> t2 = container.access(IRepoData.class, new ICallback<IRepoData>() {
			@Override
			public void process(final IRepoData t) throws Exception {
				transactionsStarted.countDown();
				waitFor(transaction2Hold);
				// System.out.println("About to read raw in t2. RepoData is " + t);
				Tests.assertThrowsWithMessage("RepoRl a already locked", RedoTransactionException.class, new Runnable() {
					@Override
					public void run() {
						t.readRaw(new RawSingleSource("a/b"));
					}
				});
			}
		});
		waitFor(transactionsStarted);
		transaction1CanRead.countDown();
		waitFor(transaction1HasRead); // at this point, t1 has read but not finished
		assertFalse(t1.isDone());
		transaction2Hold.countDown();
		t2.get();
		transaction1CanFinish.countDown();
		t1.get();
	}

	public void testLinkedMakesUnique_RepoData_LinkedRepoReader_HasPulled_WithEachTransaction() {
		container.register(IRepoData.class, IRepoDataFactory.Utils.localFactory(linkedFacard, repoLocator, hasPulledRaw));

		EnsureUniqueParameter<IRepoData> containerMemory = ICallback.Utils.<IRepoData> ensureUniqueParameter();
		List<ITransaction<?>> transactions = Lists.newList();
		for (int i = 0; i < 10; i++)
			transactions.add(container.access(IRepoData.class, containerMemory));
		for (ITransaction<?> transaction : transactions)
			transaction.get();

		ensureUniqueReaders(containerMemory.list);
		ensureUniqueHasPulled(containerMemory.list);
	}

	public void testSimpleMakesUnique_RepoData_SimpleRepoReader() {
		container.register(IRepoData.class, IRepoDataFactory.Utils.simpleFactory(linkedFacard));

		EnsureUniqueParameter<IRepoData> containerMemory = ICallback.Utils.<IRepoData> ensureUniqueParameter();
		List<ITransaction<?>> transactions = Lists.newList();
		for (int i = 0; i < 10; i++)
			transactions.add(container.access(IRepoData.class, containerMemory));
		for (ITransaction<?> transaction : transactions)
			transaction.get();

		Set<IRepoReaderImplementor> readers = ensureUniqueReaders(containerMemory.list);
		for (IRepoReaderImplementor reader : readers)
			assertTrue(reader instanceof SimpleRepoReader);

	}

	private Set<IRepoReaderImplementor> ensureUniqueReaders(List<IRepoData> list) {
		Set<IRepoReaderImplementor> readers = Sets.newSet();
		for (IRepoData data : list) {
			RepoData repoData = (RepoData) data;
			IRepoReaderImplementor linkedRepoReader = repoData.repoReader;
			readers.add(linkedRepoReader);
		}
		assertEquals(list.size(), readers.size());
		return readers;
	}

	private void ensureUniqueHasPulled(List<IRepoData> list) {
		Set<ITransactionalMutableSimpleSet<String>> hasPulledSet = Sets.newSet();
		for (IRepoData data : list) {
			RepoData repoData = (RepoData) data;
			LinkedRepoReader linkedRepoReader = (LinkedRepoReader) repoData.repoReader;
			hasPulledSet.add(linkedRepoReader.hasPulled);
		}
		assertEquals(list.size(), hasPulledSet.size());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		container = new Container(transactionManager, null) ;
	}

}
