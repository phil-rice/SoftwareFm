/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import junit.framework.TestCase;

import org.junit.Test;
import org.softwareFm.crowdsource.server.doers.internal.SaltProcessor;

public class SaltProcessorTest extends TestCase {

	@Test
	public void test() {
		SaltProcessor saltProcessor = new SaltProcessor();
		String uuid1 = saltProcessor.makeSalt();
		String uuid2 = saltProcessor.makeSalt();
		assertFalse(uuid1.equals(uuid2));

		assertTrue(saltProcessor.invalidateSalt(uuid1));
		assertTrue(saltProcessor.invalidateSalt(uuid2));

		assertFalse(saltProcessor.invalidateSalt(uuid1));
		assertFalse(saltProcessor.invalidateSalt(uuid2));
	}

}