package org.softwareFm.display.simpleButtons;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.resources.IResourceGetter;

public interface IButtonParent {

	Composite getButtonComposite();

	ImageRegistry getImageRegistry();

	IResourceGetter getResourceGetter();

	void buttonAdded(IHasControl button);

	public static class Utils {
		public static IButtonParent buttonParent(final Composite parent, final ImageRegistry imageRegistry, final IResourceGetter resourceGetter) {
			return new IButtonParent() {

				@Override
				public IResourceGetter getResourceGetter() {
					return resourceGetter;
				}

				@Override
				public ImageRegistry getImageRegistry() {
					return imageRegistry;
				}

				@Override
				public Composite getButtonComposite() {
					return parent;
				}

				@Override
				public void buttonAdded(IHasControl button) {
				}
			};
		}
	}
}
