package org.softwareFm.display.displayer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.composites.CompositeConfig;

public class CompressedTextDisplayerFactory extends AbstractCompressedTextDisplayerFactory<CompressedText> {

	@Override
	protected CompressedText makeText(Composite parent, int null1, CompositeConfig compositeConfig) {
		CompressedText text = new CompressedText(parent, SWT.NULL, compositeConfig);
		return text;
	}

}
