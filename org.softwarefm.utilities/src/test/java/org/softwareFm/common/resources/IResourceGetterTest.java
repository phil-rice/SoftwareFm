/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.resources;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.resources.ResourceGetterMock;

public class IResourceGetterTest extends TestCase {

	public void testMock() {
		IFunction1<String, IResourceGetter> fn = IResourceGetter.Utils.mock(new ResourceGetterMock("a", "da", "b", "db"), "one", new ResourceGetterMock("a", "1a"), "two", new ResourceGetterMock("a", "2a"));
		assertEquals("da", IResourceGetter.Utils.get(fn, null, "a"));
		assertEquals("1a", IResourceGetter.Utils.get(fn, "one", "a"));
		assertEquals("2a", IResourceGetter.Utils.get(fn, "two", "a"));
		assertEquals("db", IResourceGetter.Utils.get(fn, "one", "b"));
	}

}