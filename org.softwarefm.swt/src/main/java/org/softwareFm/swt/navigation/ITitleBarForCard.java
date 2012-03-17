/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.navigation;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.navigation.internal.NavIconFunction;

public interface ITitleBarForCard extends IHasControl {

	void setUrl(ICard card);

	public static class Utils {
		public static IFunction1<Map<String, Object>, Image> navIconFn(IFunction1<String, IResourceGetter> resourceGetterFn, IFunction1<String, Image> imageFn) {
			return new NavIconFunction(resourceGetterFn, imageFn);
		}
	}
}