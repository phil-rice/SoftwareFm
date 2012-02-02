/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import org.softwareFm.swt.card.LineItem;

public class CardValueFunctionTest extends AbstractResourceGetterFnTest {

	private CardValueFunction function;

	public void testFunctionUsesPatternInResourceIfRelevantKey0IsKey1IsValue() throws Exception {
		assertEquals("aa1", function.apply(null, new LineItem("one", "a", "value")));
		assertEquals("bvalue1", function.apply(null, new LineItem("one", "b", "value")));
		assertEquals("aa2", function.apply(null, new LineItem("two", "a", "value")));
		assertEquals("bvalue2", function.apply(null, new LineItem("two", "b", "value")));
		assertEquals("aax", function.apply(null, new LineItem(null, "a", "value")));
		assertEquals("bvaluex", function.apply(null, new LineItem(null, "b", "value")));
	}

	public void testFunctionProvidesValueIfNoKey() throws Exception {
		assertEquals("value", function.apply(null, new LineItem(null, "q", "value")));
		assertEquals("value", function.apply(null, new LineItem(null, "camelCase", "value")));
	}

	public void testReplacesColonWIthUnderscores() throws Exception {
		assertEquals("xwi_th,value2", function.apply(null, new LineItem("one", "wi:th", "value")));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		function = new CardValueFunction(resourceGetterFn, "x{0}");
	}
}