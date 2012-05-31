package org.softwarefm.eclipse.selection;

import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;

public class SelectedBindingAdapter implements ISelectedBindingListener {

	@Override
	public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
	}

	@Override
	public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
	}

	@Override
	public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
	}

	@Override
	public void projectDetermined(ProjectData projectData, int selectionCount) {
	}

	@Override
	public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
	}

	@Override
	public void notJavaElement(int selectionCount) {
	}

}
