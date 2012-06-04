package org.softwarefm.labelAndText;

public interface IButtonConfig {
	String key();

	boolean canExecute(IGetTextWithKey textWithKey);

	void execute() throws Exception;

	public static class Utils {
		public static IButtonConfig alwaysEnable(final String key, final Runnable runnable) {
			return new IButtonConfig() {
				public boolean canExecute(IGetTextWithKey textWithKey) {
					return true;
				}

				public void execute() throws Exception {
					runnable.run();
				}

				public String key() {
					return key;
				}
			};
		}

		public static IButtonConfig sysout(final String key) {
			return new IButtonConfig() {
				public boolean canExecute(IGetTextWithKey textWithKey) {
					return true;
				}

				public void execute() throws Exception {
					sysout("Pressed: " + key);
				}

				public String key() {
					return key;
				}
			};
		}
	}
}
