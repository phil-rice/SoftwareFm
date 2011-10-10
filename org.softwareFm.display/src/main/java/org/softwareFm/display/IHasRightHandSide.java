package org.softwareFm.display;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;

public interface IHasRightHandSide extends IHasComposite {

	void makeVisible(Control control);

	Control getVisibleControl();

	public static class Utils {

		public static IHasRightHandSide makeRightHandSide(final Composite parent) {
			return new IHasRightHandSide() {
				private final Composite holder = Swts.newComposite(parent, SWT.NULL, "SimpleRightHandSide");
				private final StackLayout layout = new StackLayout();

				{
					holder.setLayout(layout);
				}

				@Override
				public Control getControl() {
					return holder;
				}

				@Override
				public Composite getComposite() {
					return holder;
				}

				@Override
				public void makeVisible(Control control) {
					layout.topControl = control;
					holder.layout();
				}

				@Override
				public Control getVisibleControl() {
					return layout.topControl;
				}
			};
		}
	}
}
