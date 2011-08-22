package org.arc4eclipse.displayJarPath;

import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.text.ConfigForTitleAnd;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Composite;

public class DisplayJarPanel extends Composite {

	private final TitleAndTextField pathField;
	private final TitleAndTextField nameField;
	private final TitleAndTextField digestField;
	private final TitleAndTextField urlField;
	private final TitleAndTextField projectField;

	public DisplayJarPanel(Composite parent, int style, DisplayerContext context) {
		super(parent, style);
		ConfigForTitleAnd config = context.configForTitleAnd;
		this.projectField = new TitleAndTextField(config, parent, DisplayJarConstants.project);
		this.pathField = new TitleAndTextField(config, parent, DisplayJarConstants.path);
		this.nameField = new TitleAndTextField(config, parent, DisplayJarConstants.name);
		this.digestField = new TitleAndTextField(config, parent, DisplayJarConstants.digest);
		this.urlField = new TitleAndTextField(config, parent, DisplayJarConstants.url);
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
