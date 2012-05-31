/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.eclipse.swt;

import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.functions.IFunction1;

public interface IHasControl {

	Control getControl();

	public static class Utils {
		public static IHasControl toHasControl(final Control control) {
			return new IHasControl() {
				@Override
				public Control getControl() {
					return control;
				}
			};
		}

		public static IFunction1<List<IHasControl>, List<Control>> toListOfControls() {
			return new IFunction1<List<IHasControl>, List<Control>>() {
				@Override
				public List<Control> apply(List<IHasControl> from) throws Exception {
					return Lists.map(from, IHasControl.Utils.toControl());
				}
			};
		}

		public static IFunction1<IHasControl, Control> toControl() {
			return new IFunction1<IHasControl, Control>() {

				@Override
				public Control apply(IHasControl from) throws Exception {
					return from.getControl();
				}
			};
		}

	}

}