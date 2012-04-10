package org.softwareFm.swt;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.eclipse.usage.internal.ApiAndSwtTest;

public class ISwtSoftwareFmFactoryTest extends ApiAndSwtTest {

	public void testCanCalloutToContainerFromSwtFunction() throws Throwable {
		final IContainer localContainer = getLocalContainer();
		final Thread threadMain = Thread.currentThread();
		final AtomicReference<Thread> thread1 = new AtomicReference<Thread>();
		final AtomicReference<IGitReader> reader1 = new AtomicReference<IGitReader>();
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch inSwtLatch1 = new CountDownLatch(1);
		final CountDownLatch swtLatch1 = new CountDownLatch(1);
		final CountDownLatch inLatch2 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		final CountDownLatch inSwtLatch2 = new CountDownLatch(1);
		final CountDownLatch swtLatch2 = new CountDownLatch(1);
		final CountDownLatch finished = new CountDownLatch(1);
		final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
		new Thread() {
			@Override
			public void run() {
				try {
					ITransaction<String> bigTransaction = localContainer.accessWithCallbackFn(IGitReader.class, new IFunction1<IGitReader, String>() {
						@Override
						public String apply(final IGitReader from) throws Exception {
							thread1.set(Thread.currentThread());
							reader1.set(from);
							return localContainer.access(IGitReader.class, new IFunction1<IGitReader, String>() {
								@Override
								public String apply(IGitReader from) throws Exception {
									System.out.println("Started nested main");
									assertEquals(thread1.get(), Thread.currentThread());
									assertEquals(reader1.get(), from);
									latch1.await();
									System.out.println("Finished nested main");
									return "from 1";
								}

							}).get();

						}
					}, new ISwtFunction1<String, String>() {
						@Override
						public String apply(String from) throws Exception {
							System.out.println("Started swt 1");
							assertEquals("from 1", from);
							assertEquals(threadMain, Thread.currentThread());
							inSwtLatch1.countDown();
							swtLatch1.await();
							localContainer.accessWithCallbackFn(IGitReader.class, new IFunction1<IGitReader, String>() {
								@Override
								public String apply(IGitReader from) throws Exception {
									System.out.println("Started nested in swt");
									assertEquals(reader1.get(), from);
									assertEquals(thread1.get(), Thread.currentThread());
									inLatch2.countDown();
									latch2.await();
									return "from2";
								}
							}, new ISwtFunction1<String, String>() {
								@Override
								public String apply(String from) throws Exception {
									System.out.println("Started swt 2");
									assertEquals("from2", from);
									assertEquals(threadMain, Thread.currentThread());
									inSwtLatch2.countDown();
									swtLatch2.await();
									return "from3";
								}
							});
							return "from4";
						}
					});
					System.out.println("Starting");
					assertFalse(bigTransaction.isDone());
					System.out.println("latch1 being cleared: " + latch1);
					latch1.countDown();
					inSwtLatch1.await();
					assertFalse(bigTransaction.isDone());//at this point in the Swt function
					System.out.println("swtLatch1");
					swtLatch1.countDown();
					inLatch2.await();
					assertFalse(bigTransaction.isDone());//at this point in the standard function launched from the swt thread
					System.out.println("latch2");
					latch2.countDown();
					inSwtLatch2.await();
					assertFalse(bigTransaction.isDone());//at this point in the standard function launched from the swt thread
					System.out.println("swtLatch2");
					swtLatch2.countDown();
					assertEquals("from", bigTransaction.get());
					assertTrue(bigTransaction.isDone());
					
				} catch (Throwable e) {
					exception.set(e);
				} finally {
					finished.countDown();
				}
			};
		}.start();
		dispatchUntilTimeoutOrLatch(finished);
		if (exception.get() != null)
			throw exception.get();
	}

}
