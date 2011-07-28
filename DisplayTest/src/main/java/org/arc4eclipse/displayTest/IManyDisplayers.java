package org.arc4eclipse.displayTest;

import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayText.DisplayText;

public interface IManyDisplayers {

	public static class Utils {
		public static IDisplayer textDisplay() {
			return new DisplayText();
		}
	}

}
