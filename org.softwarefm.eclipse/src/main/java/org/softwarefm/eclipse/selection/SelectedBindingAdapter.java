package org.softwarefm.eclipse.selection;

import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;

public class SelectedBindingAdapter implements ISelectedBindingListener {

	public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
	}

	public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
	}

	public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
	}

	public void projectDetermined(ProjectData projectData, int selectionCount) {
	}

	public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
	}

	public void notJavaElement(int selectionCount) {
	}

}
