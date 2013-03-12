package org.softwarefm.core.composite;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;

public interface IDebugText {

	void startEvent(String text);

	void continueEvent(String text);

	public static class Utils {
		public static IDebugText debugTextComposite(Composite parent, SoftwareFmContainer<?> container) {
			return new DebugTextComposite(parent, container);
		}
	}
}
