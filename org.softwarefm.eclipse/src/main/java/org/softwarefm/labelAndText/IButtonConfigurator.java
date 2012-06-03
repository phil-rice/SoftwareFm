package org.softwarefm.labelAndText;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.utilities.resources.IResourceGetter;

public interface IButtonConfigurator {

	void configure(Composite parent, SoftwareFmContainer<?> container);

	public static class Utils {
		public static void addButton(Composite parent, SoftwareFmContainer<?> container, String key, Runnable runnable) {
			Swts.Buttons.makePushButton(parent, IResourceGetter.Utils.getOrException(container.resourceGetter, key), runnable).setData(key);
		}

		public static IButtonConfigurator okCancel(final Runnable ok, final Runnable cancel) {
			return new IButtonConfigurator() {

				public void configure(Composite parent, SoftwareFmContainer<?> container) {
					addButton(parent, container, SwtConstants.cancelButton, cancel);
					addButton(parent, container, SwtConstants.okButton, ok);
				}
			};
		}

		public static IButtonConfigurator ok(final Runnable ok) {
			return new IButtonConfigurator() {

				public void configure(Composite parent, SoftwareFmContainer<?> container) {
					addButton(parent, container, SwtConstants.okButton, ok);
				}
			};
		}
	}

}
