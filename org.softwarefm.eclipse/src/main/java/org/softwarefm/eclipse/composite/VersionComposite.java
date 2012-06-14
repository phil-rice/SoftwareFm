package org.softwarefm.eclipse.composite;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.labelAndText.TextAndBrowserComposite;
import org.softwarefm.utilities.constants.CommonConstants;

public class VersionComposite extends TextAndBrowserComposite {

	public VersionComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
	}

	@Override
	public void projectDetermined(ProjectData projectData, int selectionCount) {
		setText("");
		String url = CommonConstants.softwareFmHostAndPrefix + "project/" + projectData.groupId + "/" + projectData.artifactId + "/" + projectData.version;
		setUrl(url);
	}

	@Override
	public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
		setText(searchingMsg());
	}

	@Override
	public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		killLastLineAndappendText(digestDeterminedMsg(fileNameAndDigest) + "\n" + searchingMsg());
	}

	@Override
	public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		setText(fileNameAndDigest.file + " was not defined in a jar");
		clearBrowser();
	}

	@Override
	public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		killLastLineAndappendText(fileNameAndDigest.file + " has not been added to softwareFm\n" + "To add it follow <these instructions>");
		setUrl(digestUrl(fileNameAndDigest));
	}
}
