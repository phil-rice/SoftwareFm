package org.softwareFm.scanView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.core.plugin.SoftwareFmActivator;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;

public class ScanView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		ConfigForTitleAnd config = SoftwareFmActivator.getDefault().getConfigForTitleAnd(parent.getDisplay());
		new ScanComposite(parent, SWT.NULL, config);

	}

	@Override
	public void setFocus() {

	}
}
