package org.softwarefm.display;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.swtBasics.IHasControl;
import org.softwarefm.display.impl.SmallButtonDefn;
import org.softwarefm.display.smallButtons.ISmallButtonFactory;
import org.softwarefm.display.smallButtons.ImageButtonConfig;

public class JarSmallButtonFactory implements ISmallButtonFactory {
	@Override
	public IHasControl create(Composite parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig config) {
		Label control = new Label(parent, SWT.NULL);
		control.setText("X");
		return IHasControl.Utils.toHasControl(control);
	}
}
