package org.softwareFm.displayCore.api;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.impl.DisplayContainerButtons;
import org.softwareFm.swtBasics.IHasComposite;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.resources.IResourceGetter;

public interface IDisplayContainerButtons extends IHasComposite, IButtonParent {
	public static class Utils {
		public static IDisplayContainerButtons makeButtons(final Composite parent, final ImageRegistry imageRegistry, final IResourceGetter resourceGetter) {
			return new DisplayContainerButtons(parent, resourceGetter, imageRegistry);
		}
	}
}
