/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import junit.framework.TestCase;

import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.resources.ResourceGetterMock;
import org.softwareFm.swt.card.LineItem;

public class LineItemHideFunctionTest extends TestCase {

	private IFunction1<String, IResourceGetter> resourceGetterFn;

	public void test() throws Exception {
		checkLineHide(false, "h1", null);
		checkLineHide(false, "h2", null);
		checkLineHide(false, "h3", null);

		checkLineHide(true, "h1", "one");
		checkLineHide(true, "h2", "one");
		checkLineHide(false, "h3", "one");
	}

	private void checkLineHide(boolean expected, String key, String cardType) throws Exception {
		assertEquals(expected, new LineItemHideFunction(resourceGetterFn, "hide").apply(null, new LineItem(cardType, key, "irrelevant")).booleanValue());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		resourceGetterFn = new IFunction1<String, IResourceGetter>() {
			@Override
			public IResourceGetter apply(String from) throws Exception {
				if ("one".equals(from))
					return new ResourceGetterMock("hide", "h1,h2");
				else
					return new ResourceGetterMock();
			}
		};

	}

}