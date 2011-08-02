package org.arc4eclipse.displayJarPath;

import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class DisplayJarPanel extends Composite {

	public DisplayJarPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout());
		final TitleAndTextField pathField = new TitleAndTextField(this, SWT.NULL, bindingContext.images, "Jar Path", false);
		final TitleAndTextField nameField = new TitleAndTextField(this, SWT.NULL, bindingContext.images, "Jar Name", false);
		final TitleAndTextField digestField = new TitleAndTextField(this, SWT.NULL, bindingContext.images, "Digest", false);
		BindingRipperResult ripped = (BindingRipperResult) this.context.get(DisplayCoreConstants.ripperResult);
		if (ripped != null) {
			IPath path = ripped.path;
			if (path != null) {
				pathField.setText(path.toFile().getPath());
				nameField.setText(path.toFile().getName());
				digestField.setText(ripped.hexDigest);
			}
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
	}

}
