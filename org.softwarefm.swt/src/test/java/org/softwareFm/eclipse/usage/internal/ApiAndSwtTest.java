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
	protected ITransactionManager getLocalTransactionManager() {
		return ISwtSoftwareFmFactory.Utils.getSwtTransactionManager(display, CommonConstants.testTimeOutMs);
	}

	@Override
	/** Needed because there are tests that have callbacks running on the server*/
	protected ITransactionManager getServerTransactionManager() {
		return ISwtSoftwareFmFactory.Utils.getSwtTransactionManager(display, CommonConstants.testTimeOutMs);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.shell = new Shell();
		this.display = shell.getDisplay();
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			Swts.Dispatch.dispatchUntilQueueEmpty(display);
		} finally {
			shell.dispose();
			super.tearDown();
		}
	}

	protected void dispatchUntilTimeoutOrLatch(CountDownLatch latch) {
		Swts.Dispatch.dispatchUntilTimeoutOrLatch(display, latch, CommonConstants.testTimeOutMs);
	}

	protected void dispatchUntilQueueEmpty() {
		Swts.Dispatch.dispatchUntilQueueEmpty(display);

	}

	protected void dispatchUntil(Callable<Boolean> callable) {
		Swts.Dispatch.dispatchUntil(display, CommonConstants.testTimeOutMs, callable);
	}

	@Override
	protected Map<String, Callable<Object>> getDefaultUserValues() {
		return Maps.with(super.getDefaultUserValues(), JarAndPathConstants.projectCryptoKey, Callables.valueFromList(projectCryptoKey0, projectCryptoKey1, projectCryptoKey2, projectCryptoKey3));
	}
}
