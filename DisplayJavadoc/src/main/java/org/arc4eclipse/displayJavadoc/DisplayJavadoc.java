package org.arc4eclipse.displayJavadoc;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DisplayJavadoc extends AbstractDisplayerWithLabel<JavadocPanel> {

	@Override
	public String getNameSpace() {
		return RepositoryConstants.javadocKey;
	}

	@Override
	public JavadocPanel createLargeControl(DisplayerContext context, Composite parent, String entity, NameSpaceAndName nameSpaceAndName, String title) {
		return new JavadocPanel(parent, SWT.BORDER, context, entity, nameSpaceAndName, title);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, JavadocPanel largeControl, Object value) {
		largeControl.setValue(bindingContext.url, getBindingRipperResult(bindingContext), Strings.nullSafeToString(value));

	}
}