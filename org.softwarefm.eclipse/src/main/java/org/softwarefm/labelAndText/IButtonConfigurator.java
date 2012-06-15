package org.softwarefm.labelAndText;

import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.TextKeys;

public interface IButtonConfigurator {

	void configure(SoftwareFmContainer<?> container, IButtonCreator creator);

	public static class Utils {
		public static IButtonConfigurator make(final IButtonConfig...configs){
			return new IButtonConfigurator() {
				public void configure(SoftwareFmContainer<?> container, IButtonCreator creator) {
					for (IButtonConfig config: configs)
						creator.createButton(config);
				}
			};
		}
		

		public static IButtonConfigurator okCancel(final Runnable ok, final Runnable cancel) {
			return new IButtonConfigurator() {
				public void configure(SoftwareFmContainer<?> container, IButtonCreator creator) {
					creator.createButton(IButtonConfig.Utils.alwaysEnable(TextKeys.btnSharedCancel, cancel));
					creator.createButton(IButtonConfig.Utils.alwaysEnable(TextKeys.btnSharedOk, ok));
				}
			};
		}

		public static IButtonConfigurator ok(final Runnable ok) {
			return new IButtonConfigurator() {
				public void configure(SoftwareFmContainer<?> container, IButtonCreator creator) {
					creator.createButton(IButtonConfig.Utils.alwaysEnable(TextKeys.btnSharedOk, ok));
				}
			};
		}
	}

}
