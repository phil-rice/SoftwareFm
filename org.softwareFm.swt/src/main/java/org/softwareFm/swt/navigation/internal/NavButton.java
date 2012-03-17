/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.navigation.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.swt.composites.IHasControl;

public class NavButton implements IHasControl {
	private final Label label;

	public NavButton(Composite parent, final String url, final ICallback<String> callbackToGotoUrl) {
		String title = Functions.call(Strings.lastSegmentFn("/"), url);
		label = new Label(parent, SWT.NULL);
		label.setAlignment(SWT.LEFT);
		label.setText(title);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				ICallback.Utils.call(callbackToGotoUrl, url);
			}
		});
	}

	@Override
	public Control getControl() {
		return label;
	}
}