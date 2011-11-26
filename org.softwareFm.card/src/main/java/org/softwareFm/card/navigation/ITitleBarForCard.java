/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.navigation;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.navigation.internal.NavIconFunction;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public interface ITitleBarForCard extends IHasControl {

	void setUrl(ICard card);
	

	public static class Utils {
		public static IFunction1<Map<String, Object>, Image> navIconFn(IFunction1<String, IResourceGetter> resourceGetterFn, IFunction1<String, Image> imageFn) {
			return new NavIconFunction(resourceGetterFn, imageFn);
		}
	}
}