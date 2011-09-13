package org.softwarefm.display.displayer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.swtBasics.IHasControl;
import org.softwarefm.display.impl.DisplayerDefn;

public class TextDisplayer implements IDisplayer {

	@Override
	public IHasControl create(Composite parent, DisplayerDefn defn, int style) {
		Text control = new Text(parent, style);
		control.setText(defn.dataKey);
		return IHasControl.Utils.toHasControl(control);
	}

}
