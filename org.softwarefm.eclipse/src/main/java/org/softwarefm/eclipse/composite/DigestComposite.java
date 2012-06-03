package org.softwarefm.eclipse.composite;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.labelAndText.TextAndBrowserComposite;

public class DigestComposite extends TextAndBrowserComposite {

	public DigestComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
	}

	@Override
	public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		killLastLineAndappendText(unknownDigestMsg(fileNameAndDigest));
	}

	@Override
	public void projectDetermined(ProjectData projectData, int selectionCount) {
		killLastLineAndappendText(projectDeterminedMsg(projectData) + "\nIf you think this is linked to the wrong data, follow <these instructions>");
	}

	@Override
	public void notJavaElement(int selectionCount) {
		killLastLineAndappendText(notJavaElementMsg());
		clearBrowser();
	}

	@Override
	public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		killLastLineAndappendText(notInAJarMsg(fileNameAndDigest));
	}

	@Override
	public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
		setText(searchingMsg());
	}

	@Override
	public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		killLastLineAndappendText(digestDeterminedMsg(fileNameAndDigest) + "\n" + searchingMsg());
		setUrl(digestUrl(fileNameAndDigest));
	}

}
