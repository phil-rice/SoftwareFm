package org.arc4eclipse.displayJarPath;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayJarPath implements IDisplayer {

	@Override
	public String getNameSpace() {
		return RepositoryConstants.jarPathKey;
	}

	@Override
	public Control makeCompositeAsChildOf(Composite parent, final BindingContext bindingContext, final NameSpaceNameAndValue nameSpaceNameAndValue) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout());
		final TitleAndTextField pathField = new TitleAndTextField(composite, SWT.NULL, bindingContext.images, "Jar Path", false);
		final TitleAndTextField nameField = new TitleAndTextField(composite, SWT.NULL, bindingContext.images, "Jar Name", false);
		final TitleAndTextField digestField = new TitleAndTextField(composite, SWT.NULL, bindingContext.images, "Digest", false);
		BindingRipperResult ripped = (BindingRipperResult) bindingContext.context.get(DisplayCoreConstants.ripperResult);
		if (ripped != null) {
			IPath path = ripped.path;
			if (path != null) {
				pathField.setText(path.toFile().getPath());
				nameField.setText(path.toFile().getName());
				digestField.setText(ripped.hexDigest);
			}
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
		return composite;
	}
}