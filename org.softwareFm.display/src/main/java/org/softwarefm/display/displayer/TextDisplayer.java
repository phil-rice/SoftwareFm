package org.softwarefm.display.displayer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.Swts;
import org.softwarefm.display.impl.DisplayerDefn;

public class TextDisplayer implements IDisplayer {

	@Override
	public IHasControl create(Composite parent, final DisplayerDefn defn, int style) {
		Composite holder = new Composite(parent, style){
			@Override
			public String toString() {
				return defn.dataKey+":"+super.toString();
			}
		};
		Text control = new Text(holder, style);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(holder);
		control.setText(defn.dataKey);
		return IHasControl.Utils.toHasControl(holder);
	}

}
