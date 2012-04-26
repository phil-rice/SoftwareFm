package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.concurrent.CountDownLatch;

import org.softwareFm.crowdsource.api.internal.Container;
import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.IRepoDataFactory;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.tests.Tests;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.transaction.RedoTransactionException;

public class RepoDataRedoContainerTest extends RepoTest {

	private Container container;

	public void testIfTryToReadSameFileInTwoTransactionsGetRedoTransactionExceptionInSecond() throws Exception {
		initRepos(gitFacard, "a");
		putFile("a/b", null, v11, v12);
		commitRepos(gitFacard, "a");

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
//				System.out.println("About to read raw in t1. RepoData is " + t);
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
//				System.out.println("About to read raw in t2. RepoData is " + t);
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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		container = new Container(transactionManager, null) {
		};
		container.register(IRepoData.class, IRepoDataFactory.Utils.factory(gitFacard));
	}

}
