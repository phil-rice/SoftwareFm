package org.softwarefm.eclipse.selection;

import java.io.File;

import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;

public class SelectedBindingAdapter implements ISelectedBindingListener {

	public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
	}

	public void digestDetermined(FileAndDigest fileAndDigest, int selectionCount) {
	}

	public void notInAJar(File file, int selectionCount) {
	}

	public void projectDetermined(ProjectData projectData, int selectionCount) {
	}

	public void unknownDigest(FileAndDigest fileAndDigest, int selectionCount) {
	}

	public void notJavaElement(int selectionCount) {
	}

}
