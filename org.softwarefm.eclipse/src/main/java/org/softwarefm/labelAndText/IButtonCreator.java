package org.softwarefm.labelAndText;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.utilities.resources.IResourceGetter;

public interface IButtonCreator {

	void createButton(IButtonConfig buttonConfig, Object... args);

	public static class Utils {

		public static IButtonCreator creator(final Composite parent, final IResourceGetter resourceGetter) {
			return new IButtonCreator() {
				public void createButton(final IButtonConfig buttonConfig, Object... args) {
					Swts.Buttons.makePushButton(parent, IResourceGetter.Utils.getMessageOrException(resourceGetter, buttonConfig.key(), args), buttonConfig).setData(buttonConfig);
				}
			};
		}
	}

}
