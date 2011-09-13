package org.softwarefm.display;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.swtBasics.IHasControl;
import org.softwarefm.display.impl.SmallButtonDefn;
import org.softwarefm.display.smallButtons.ISmallButtonFactory;

public class SmallButtonFactory implements ISmallButtonFactory {

	@Override
	public IHasControl create(Composite parent, SmallButtonDefn smallButtonDefn, int style) {
		Label control = new Label(parent, style);
		control.setText("X");
		return IHasControl.Utils.toHasControl(control);
	}

}
