package org.arc4eclipse.displayJarPath;

import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DisplayJarPanel extends Composite {

	private final TitleAndTextField pathField;
	private final TitleAndTextField nameField;
	private final TitleAndTextField digestField;
	private final TitleAndTextField urlField;
	private final TitleAndTextField projectField;

	public DisplayJarPanel(Composite parent, int style, DisplayerContext context) {
		super(parent, style);
		IImageFactory imageFactory = context.imageFactory;
		projectField = new TitleAndTextField(this, SWT.NULL, "Project", false);
		projectField.addHelpButton(DisplayJarConstants.helpProject);
		pathField = new TitleAndTextField(this, SWT.NULL, "Jar Path", false);
		pathField.addHelpButton(DisplayJarConstants.helpPathField);
		nameField = new TitleAndTextField(this, SWT.NULL, "Jar Name", false);
		nameField.addHelpButton(DisplayJarConstants.helpJarName);
		digestField = new TitleAndTextField(this, SWT.NULL, "Digest", false);
		digestField.addHelpButton(DisplayJarConstants.helpDigest);
		urlField = new TitleAndTextField(this, SWT.NULL, "Url", false);
		urlField.addHelpButton(DisplayJarConstants.helpUrl);
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
				projectField.setText(ripped.javaProject == null ? null : ripped.javaProject.getElementName());
				return;

			}
		}
		pathField.setText("");
		nameField.setText("");
		digestField.setText("");
		urlField.setText(url);
		projectField.setText("");
	}

}
