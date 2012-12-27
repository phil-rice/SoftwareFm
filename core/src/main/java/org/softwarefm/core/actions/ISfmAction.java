package org.softwarefm.core.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.softwarefm.utilities.resources.IResourceGetter;

public interface ISfmAction {

	void execute();

	String toolTip();

	ImageDescriptor imageDescriptor();

	public static class Utils {
		public static ISfmAction action(final IResourceGetter resourceGetter, final Class<?> imageAnchor, final String imageKey, final String tooltipKey, final Runnable runnable) {
			return new ISfmAction() {
				String tooltipText = IResourceGetter.Utils.getOrException(resourceGetter, tooltipKey);

				public String toolTip() {
					return tooltipText;
				}

				public void execute() {
					runnable.run();
				}

				public ImageDescriptor imageDescriptor() {
					return ImageDescriptor.createFromFile(imageAnchor, imageKey);
				}
			};
		}
	}
}
