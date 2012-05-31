package org.softwarefm.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.utilities.constants.CommonConstants;

public class ProjectPanel extends TextAndBrowserPanel {

	public ProjectPanel(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
	}

	@Override
	public void projectDetermined(ProjectData projectData, int selectionCount) {
		killLastLineAndappendText(projectDeterminedMsg(projectData));
		String url = CommonConstants.softwareFmHostAndPrefix + "project/" + projectData.groupId + "/" + projectData.artefactId;
		setUrl(url);
	}

	@Override
	public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
		setText(searchingMsg());
	}

	@Override
	protected void notJavaElement(int selectionCount) {
		killLastLineAndappendText(notJavaElementMsg());
		clearBrowser();
	}

	@Override
	public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		setText(notInAJarMsg(fileNameAndDigest));
		clearBrowser();
	}

	@Override
	public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		killLastLineAndappendText(digestDeterminedMsg(fileNameAndDigest) + "\n" + searchingMsg());
	}

	@Override
	public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		killLastLineAndappendText(unknownDigestMsg(fileNameAndDigest));
		setUrl(digestUrl(fileNameAndDigest));
	}
}
