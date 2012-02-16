/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.server;

import java.io.File;

import junit.framework.TestCase;

import org.softwareFm.common.collections.Files;
import org.softwareFm.common.tests.Tests;

abstract public class TemporaryFileTest extends TestCase {
	protected File root;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		root = Tests.makeTempDirectory(getClass().getSimpleName());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (root.exists())
			assertTrue(Files.deleteDirectory(root));
	}
}