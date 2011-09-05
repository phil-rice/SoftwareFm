package org.softwareFm.displayJavadocAndSource;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.core.plugin.SelectedArtifactSelectionManager;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.JavaProjects;

public class SourcePanel extends JavadocOrSourcePanel {

	public SourcePanel(Composite parent, int style, DisplayerContext displayerContext, DisplayerDetails displayerDetails, String linkKey) {
		super(parent, style, displayerContext, displayerDetails, linkKey);
	}

	@Override
	public void sendToEclipse(String value) {
		BindingRipperResult ripped = (BindingRipperResult) bindingContext.context.get(DisplayCoreConstants.ripperResult);
		BindingRipperResult uptoDate = SelectedArtifactSelectionManager.reRip(ripped);
		if (uptoDate != null)
			JavaProjects.setSourceAttachment(uptoDate.javaProject, uptoDate.classpathEntry, value);
	}

	@Override
	public void clearEclipseValue() {
		sendToEclipse(null);
	}

	@Override
	protected void openEclipseValue(String eclipseValue) {
		browseOrOpenFile(eclipseValue);
	}

	@Override
	protected String findEclipseValue(BindingContext bindingContext) throws Exception {
		BindingRipperResult ripped = (BindingRipperResult) bindingContext.context.get(DisplayCoreConstants.ripperResult);
		BindingRipperResult uptoDate = SelectedArtifactSelectionManager.reRip(ripped);
		if (uptoDate != null)
			return uptoDate.packageFragment.getSourceAttachmentPath().toOSString();
		else
			return null;
	}

}
