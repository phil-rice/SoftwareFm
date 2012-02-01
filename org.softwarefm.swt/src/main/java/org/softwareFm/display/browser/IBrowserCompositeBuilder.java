/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.display.browser;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;

public interface IBrowserCompositeBuilder extends IBrowserComposite {
	IBrowserPart register(String feedType, IFunction1<Composite, IBrowserPart> displayCreator);
}