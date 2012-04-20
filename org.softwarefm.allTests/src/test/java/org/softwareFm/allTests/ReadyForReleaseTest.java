/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.allTests;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.eclipse.plugin.Activator;

public class ReadyForReleaseTest extends TestCase {

	public void testNotLocal() {
		assertFalse(Activator.localServer);
	}

	public void testPorts() {
		assertEquals(80, CommonConstants.serverPort);
	}

	public void testTimeout() {
		assertEquals(3000, CommonConstants.testTimeOutMs);
	}
}