/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
	//need to share transaction manager between server and local.
	//shared because in tests we can hop from one transaction to another, when for example we read locally and write to the server
	protected ITransactionManager makeTransactionManager() {
		return ISwtSoftwareFmFactory.Utils.getSwtTransactionManager(display, CommonConstants.threadPoolSizeForTests, CommonConstants.testTimeOutMs);
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
		return inTransaction || inSwtCallbackFunction ? 0 : 0;
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
				// System.out.println("jobs: " + jobs + " target: " + count + " in sync: " + ISwtSoftwareFmFactory.Utils.inSwtCallbackFunction(display) + " swts finished: " + swtFunctionsFinished);

				return callable.call() && jobs == count && swtFunctionsFinished;
			}
		});
	}

	@Override
	protected Map<String, Callable<Object>> getDefaultUserValues() {
		return Maps.with(super.getDefaultUserValues(), JarAndPathConstants.projectCryptoKey, Callables.valueFromList(projectCryptoKey0, projectCryptoKey1, projectCryptoKey2, projectCryptoKey3));
	}
}