package org.arc4eclipse.displayJavadoc;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DisplayJavadoc extends AbstractDisplayerWithLabel<JavaDocPanel> {

	@Override
	public String getNameSpace() {
		return RepositoryConstants.javadocKey;
	}

	@Override
	public JavaDocPanel createLargeControl(DisplayerContext context, Composite parent, NameSpaceAndName nameSpaceAndName, String title) {
		return new JavaDocPanel(parent, SWT.BORDER, context, nameSpaceAndName, title);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, JavaDocPanel largeControl, Object value) {
		largeControl.setValue(bindingContext.url, Strings.nullSafeToString(value));

	}
}