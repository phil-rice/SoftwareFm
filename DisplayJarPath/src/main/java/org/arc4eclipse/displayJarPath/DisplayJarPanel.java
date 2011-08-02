package org.arc4eclipse.displayJarPath;

import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class DisplayJarPanel extends Composite {

	private final TitleAndTextField pathField;
	private final TitleAndTextField nameField;
	private final TitleAndTextField digestField;
	private final TitleAndTextField urlField;

	public DisplayJarPanel(Composite parent, int style, DisplayerContext context) {
		super(parent, style);
		setLayout(new GridLayout());
		IImageFactory imageFactory = context.imageFactory;
		pathField = new TitleAndTextField(this, SWT.NULL, imageFactory, "Jar Path", false);
		nameField = new TitleAndTextField(this, SWT.NULL, imageFactory, "Jar Name", false);
		digestField = new TitleAndTextField(this, SWT.NULL, imageFactory, "Digest", false);
		urlField = new TitleAndTextField(this, SWT.NULL, imageFactory, "Url", false);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
	}

	public void setValue(String url, BindingRipperResult ripped) {
		if (ripped != null) {
			IPath path = ripped.path;
			if (path != null) {
				pathField.setText(path.toFile().getPath());
				nameField.setText(path.toFile().getName());
				digestField.setText(ripped.hexDigest);
				urlField.setText(url);
				return;
			}
		}
		pathField.setText("");
		nameField.setText("");
		digestField.setText("");
		urlField.setText(url);
	}

}
