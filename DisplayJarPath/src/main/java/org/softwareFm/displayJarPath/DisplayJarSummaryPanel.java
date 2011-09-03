package org.softwareFm.displayJarPath;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.displayCore.api.BoundTitleAndTextField;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.text.TitleAndTextField;
import org.softwareFm.utilities.strings.Strings;

public class DisplayJarSummaryPanel extends Composite {

	private final TitleAndTextField txtJar;
	private final BoundTitleAndTextField txtProjectName;
	private final BoundTitleAndTextField txtOrganisationName;

	public DisplayJarSummaryPanel(Composite parent, int style, DisplayerContext displayerContext, DisplayerDetails displayerDetails) {
		super(parent, style);
		setLayout(new GridLayout());
		new Label(this, SWT.NULL).setText("Jar is here");
		txtJar = new TitleAndTextField(displayerContext.configForTitleAnd, this, DisplayJarConstants.name);
		txtProjectName = new BoundTitleAndTextField(this, displayerContext, displayerDetails.withKey("project.url"));
		txtOrganisationName = new BoundTitleAndTextField(this, displayerContext, displayerDetails.withKey("organisation.url"));
		Swts.makeGrabHorizonalAndFillGridData();
	}

	public void setValue(String url, BindingRipperResult ripped, Map<String, Object> data) {
		File file = ripped.path == null ? null : ripped.path.toFile();
		String name = file == null ? "" : file.getName();
		txtJar.setText(name);
		txtProjectName.setText(Strings.nullSafeToString(data == null ? null : data.get("project.url")));
		txtOrganisationName.setText(Strings.nullSafeToString(data == null ? null : data.get("organisation.url")));
	}

}
