package org.softwareFm.swtBasics;

import org.eclipse.swt.widgets.Control;

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

	}
}
