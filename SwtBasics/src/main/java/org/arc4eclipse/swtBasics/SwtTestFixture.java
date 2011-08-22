package org.arc4eclipse.swtBasics;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SwtTestFixture {

	private static Display display;

	public static Shell shell() {
		if (display == null) {
			Shell result = new Shell();
			display = result.getDisplay();
			return result;
		} else
			return new Shell(display);
	}
}
