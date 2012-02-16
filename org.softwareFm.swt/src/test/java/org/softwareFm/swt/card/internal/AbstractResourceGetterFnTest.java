/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import junit.framework.TestCase;

import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.resources.ResourceGetterMock;

abstract public class AbstractResourceGetterFnTest extends TestCase {

	protected IFunction1<String, IResourceGetter> resourceGetterFn;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		resourceGetterFn = new IFunction1<String, IResourceGetter>() {
			@Override
			public IResourceGetter apply(String from) throws Exception {
				if ("one".equals(from))
					return new ResourceGetterMock("xa", "a{0}1", "xb", "b{1}1", "xwi_th", "x{0},{1}2");
				else if ("two".equals(from))
					return new ResourceGetterMock("xa", "a{0}2", "xb", "b{1}2");
				else
					return new ResourceGetterMock("xa", "a{0}x", "xb", "b{1}x");
			}
		};

	}

}