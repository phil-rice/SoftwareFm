package org.softwareFm.eclipse.usage.internal;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.ISwtSoftwareFmFactory;
import org.softwareFm.swt.swt.Swts;

abstract public class ApiAndSwtTest extends ApiTest {

	protected final static String projectCryptoKey0 = Crypto.makeKey();
	protected final static String projectCryptoKey1 = Crypto.makeKey();
	protected final static String projectCryptoKey2 = Crypto.makeKey();
	protected final static String projectCryptoKey3 = Crypto.makeKey();

	protected Shell shell;
	protected Display display;

	@Override
	protected ITransactionManager makeLocalTransactionManager() {
		return ISwtSoftwareFmFactory.Utils.getSwtTransactionManager(display, CommonConstants.localThreadPoolSizeForTests, CommonConstants.testTimeOutMs);
	}

	@Override
	/** Needed because there are tests that have callbacks running on the server*/
	protected ITransactionManager makeServerTransactionManager() {
		return ISwtSoftwareFmFactory.Utils.getSwtTransactionManager(display, CommonConstants.serverThreadPoolSizeForTests, CommonConstants.testTimeOutMs);
	}

	@Override
	protected void setUp() throws Exception {
		this.shell = new Shell();
		this.display = shell.getDisplay();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			dispatchUntilJobsFinished();
		} finally {
			shell.dispose();
			super.tearDown();
		}
	}

	protected void dispatchUntilTimeoutOrLatch(final CountDownLatch latch) {
		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return latch.getCount() == 0;
			}
		});
	}

	private int allOtherJobsClosedCount() {
		boolean inTransaction = getLocalContainer().getTransactionManager().inTransaction();
		boolean inSwtCallbackFunction = ISwtSoftwareFmFactory.Utils.inSwtCallbackFunction(display);
		return inTransaction||inSwtCallbackFunction ? 0 : 0;
	}

	protected void dispatchUntilJobsFinished() {
		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				int jobs = getLocalContainer().activeJobs();
				int count = allOtherJobsClosedCount();
				return jobs == count;
			}
		});
	}

	Callable<Boolean> activeJobsAre(final ITransactionManager manager, final int value) {
		return new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return manager.activeJobs() == value;
			}
		};
	}

	protected void dispatchUntil(final Callable<Boolean> callable) {
		Swts.Dispatch.dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				int jobs = getLocalContainer().activeJobs();
				int count = allOtherJobsClosedCount();
				boolean swtFunctionsFinished = ISwtSoftwareFmFactory.Utils.swtFunctionsFinished();
				//				System.out.println("jobs: " + jobs + " target: " + count + " in sync: "  + ISwtSoftwareFmFactory.Utils.inSwtCallbackFunction(display) + " swts finished: " + swtFunctionsFinished);
				
				return callable.call() && jobs == count && swtFunctionsFinished;
			}
		});
	}

	@Override
	protected Map<String, Callable<Object>> getDefaultUserValues() {
		return Maps.with(super.getDefaultUserValues(), JarAndPathConstants.projectCryptoKey, Callables.valueFromList(projectCryptoKey0, projectCryptoKey1, projectCryptoKey2, projectCryptoKey3));
	}
}
