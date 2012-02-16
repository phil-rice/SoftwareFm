/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import org.softwareFm.swt.card.LineItem;

public class CardNameFunctionTest extends AbstractResourceGetterFnTest {

	private CardNameFunction function;

	public void testFunctionUsesPatternInResourceIfRelevantKey0IsRawKey1IsPretty() throws Exception {
		assertEquals("aa1", function.apply(null, new LineItem("one", "a", "irrelevant")));
		assertEquals("bB1", function.apply(null, new LineItem("one", "b", "irrelevant")));
		assertEquals("aa2", function.apply(null, new LineItem("two", "a", "irrelevant")));
		assertEquals("bB2", function.apply(null, new LineItem("two", "b", "irrelevant")));
		assertEquals("aax", function.apply(null, new LineItem(null, "a", "irrelevant")));
		assertEquals("bBx", function.apply(null, new LineItem(null, "b", "irrelevant")));
	}

	public void testFunctionPrettyPrintsIfNoKey() throws Exception {
		assertEquals("Q", function.apply(null, new LineItem(null, "q", "irrelevant")));
		assertEquals("Camel Case", function.apply(null, new LineItem(null, "camelCase", "irrelevant")));
	}

	public void testReplacesColonWIthUnderscores() throws Exception {
		assertEquals("xwi_th,Wi:th2", function.apply(null, new LineItem("one", "wi:th", "irrelevant")));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		function = new CardNameFunction(resourceGetterFn, "x{0}");
	}

}