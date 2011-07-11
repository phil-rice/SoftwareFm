package org.arc4eclipse.utilities.monitor;

import java.text.MessageFormat;

public class SysoutMonitor extends AbstractMonitor {

	private final String pattern;
	private final int quanta;

	public SysoutMonitor(String pattern, int quanta) {
		this.pattern = pattern;
		this.quanta = quanta <= 0 ? 1 : quanta;
	}

	public void processed(String message, int done, int max) {
		if ((done + 1) % quanta == 0)
			display(message, done, max);
	}

	// Separate method to allow testing
	protected void display(String message, int done, int max) {
		System.out.println(MessageFormat.format(pattern, message, done, max));
	}
}
