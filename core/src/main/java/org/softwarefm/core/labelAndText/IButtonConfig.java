package org.softwarefm.core.labelAndText;

import java.util.Collections;
import java.util.List;

public interface IButtonConfig {
	String key();

	/** returns a list of problems preventing execution. If empty can enable the button */
	List<KeyAndProblem> canExecute(IGetTextWithKey textWithKey);

	/** Will execute if called, even if canExecute would have said not. Do those checks outside this */
	void execute() throws Exception;

	public static class Utils {
		public static IButtonConfig alwaysEnable(final String key, final Runnable runnable) {
			return new IButtonConfig() {
				public List<KeyAndProblem> canExecute(IGetTextWithKey textWithKey) {
					return Collections.emptyList();
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
				public List<KeyAndProblem> canExecute(IGetTextWithKey textWithKey) {
					return Collections.emptyList();
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
