package org.softwareFm.display.displayer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.composites.CompositeConfig;

public class CompressedStyledTextDisplayerFactory extends AbstractCompressedTextDisplayerFactory<CompressedStyledText> {

	@Override
	protected CompressedStyledText makeText(Composite parent, int null1, CompositeConfig compositeConfig) {
		CompressedStyledText text = new CompressedStyledText(parent, SWT.NULL, compositeConfig);
		return text;
	}

}
