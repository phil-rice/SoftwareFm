package org.softwareFm.displayJarPath;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.softwareFmImages.ImageButtons;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.TitleAndTextField;

public class DisplayJarPanel extends Composite {

	private final TitleAndTextField pathField;
	private final TitleAndTextField nameField;
	private final TitleAndTextField digestField;
	private final TitleAndTextField urlField;
	private final TitleAndTextField projectField;

	public DisplayJarPanel(Composite parent, int style, DisplayerContext context) {
		super(parent, style);
		ConfigForTitleAnd config = context.configForTitleAnd;
		this.projectField = addNameValue(this, config, DisplayJarConstants.project);
		this.pathField = addNameValue(this, config, DisplayJarConstants.path);
		this.nameField = addNameValue(this, config, DisplayJarConstants.name);
		this.digestField = addNameValue(this, config, DisplayJarConstants.digest);
		this.urlField = addNameValue(this, config, DisplayJarConstants.url);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
	}

	private TitleAndTextField addNameValue(Composite parent, ConfigForTitleAnd config, String key) {
		TitleAndTextField titleAndTextField = new TitleAndTextField(config, parent, key);
		ImageButtons.addHelpButton(titleAndTextField, key, GeneralAnchor.helpKey);
		return titleAndTextField;
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
