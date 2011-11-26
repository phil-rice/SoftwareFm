/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.monitor;

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