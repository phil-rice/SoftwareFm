package org.softwareFm.displayJarPath;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.BoundTitleAndTextField;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.displayJavadocAndSource.JavadocOrSourcePanel;
import org.softwareFm.displayJavadocAndSource.JavadocPanel;
import org.softwareFm.displayJavadocAndSource.SourcePanel;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.softwareFmImages.ImageButtons;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.text.TitleAndTextField;
import org.softwareFm.utilities.strings.Strings;

public class DisplayJarSummaryPanel implements IHasControl {

	private final TitleAndTextField txtJar;
	private final BoundTitleAndTextField txtProjectName;
	private final BoundTitleAndTextField txtOrganisationName;
	private final JavadocOrSourcePanel javadocPanel;
	private final SourcePanel sourcePanel;
	private final Composite content;

	public DisplayJarSummaryPanel(Composite parent, int style, DisplayerContext displayerContext, DisplayerDetails displayerDetails) {
		this.content = new Composite(parent, style);
		content.setLayout(new GridLayout());
		txtJar = new TitleAndTextField(displayerContext.configForTitleAnd, content, DisplayJarConstants.name);
		txtProjectName = makeLinkUrlField(displayerContext, displayerDetails, "project.url");
		txtOrganisationName = makeLinkUrlField(displayerContext, displayerDetails, "organisation.url");
		javadocPanel = new JavadocPanel(content, SWT.NULL, displayerContext, displayerDetails.withKey("javadoc"));
		sourcePanel = new SourcePanel(content, SWT.NULL, displayerContext, displayerDetails.withKey("source"));
		Swts.makeGrabHorizonalAndFillGridData();
	}

	private BoundTitleAndTextField makeLinkUrlField(DisplayerContext displayerContext, DisplayerDetails displayerDetails, String key) {
		final BoundTitleAndTextField field = new BoundTitleAndTextField(content, displayerContext, displayerDetails.withKey(key));
		ImageButtons.addEditButton(field, displayerDetails.getSmallImageKey(), OverlaysAnchor.editKey, field.editButtonListener());
		ImageButtons.addBrowseButton(field, GeneralAnchor.browseKey, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return field.getText();
			}
		});
		return field;
	}

	public void setValue(BindingContext bindingContext) {
		BindingRipperResult ripped = (BindingRipperResult) bindingContext.context.get(DisplayCoreConstants.ripperResult);
		Map<String, Object> data = bindingContext.data;
		File file = ripped.path == null ? null : ripped.path.toFile();
		String name = file == null ? "" : file.getName();
		txtJar.setText(name);
		txtProjectName.setText(Strings.nullSafeToString(data == null ? null : data.get("project.url")));
		txtOrganisationName.setText(Strings.nullSafeToString(data == null ? null : data.get("organisation.url")));
		javadocPanel.setValue(bindingContext);
		sourcePanel.setValue(bindingContext);
	}

	@Override
	public Control getControl() {
		return content;
	}

}
