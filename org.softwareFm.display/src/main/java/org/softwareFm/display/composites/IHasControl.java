package org.softwareFm.display.composites;

import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;

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
