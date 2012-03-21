/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.swt;

import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

abstract public class SwtTest extends TestCase {

	protected Shell shell;
	protected Display display;

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



	protected void dispatchUntilQueueEmpty() {
		Swts.Dispatch.dispatchUntilQueueEmpty(display);
	}

	protected void kickAndDispatch(Future<?> future) {
		Swts.Dispatch.kickAndDispatch(display, future);
	}

}