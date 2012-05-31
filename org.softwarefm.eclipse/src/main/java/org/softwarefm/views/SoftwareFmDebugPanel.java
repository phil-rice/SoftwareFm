package org.softwarefm.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;

public class SoftwareFmDebugPanel extends SoftwareFmPanel {

	public SoftwareFmDebugPanel(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		getComposite().setLayout(new FillLayout());
		final StyledText text = new StyledText(getComposite(), SWT.WRAP | SWT.READ_ONLY);
		addListener(new ISelectedBindingListener() {
			@Override
			public void notJavaElement(int selectionCount) {
				text.append("notJavaElement: (" + selectionCount + ")\n");
			}
			@Override
			public void projectDetermined(ProjectData projectData, int selectionCount) {
				text.append("projectDetermined: (" + selectionCount + ")\n" + projectData + "\n");
			}

			@Override
			public void notInAJar(FileNameAndDigest filename, int selectionCount) {
				text.append("Not In A Jar: (" + selectionCount + ") file is: " + filename + "\n");
			}

			@Override
			public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				text.append("Digest: (" + selectionCount + ") " + fileNameAndDigest + "\n");
			}

			@Override
			public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
				text.setText("Class and method: (" + selectionCount + ")\n" + expressionData + "\n");
			}

			@Override
			public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				text.append("Unknown Digest: (" + selectionCount + ") " + fileNameAndDigest + "\n");
			}
		});
	}

}
