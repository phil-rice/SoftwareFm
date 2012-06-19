package org.softwarefm.eclipse.selection;

import java.io.File;

import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.jdtBinding.CodeData;

abstract public class SelectedBindingAdapter implements ISelectedBindingListener {

	public void codeSelectionOccured(CodeData codeData, int selectionCount) {
	}

	public void digestDetermined(FileAndDigest fileAndDigest, int selectionCount) {
	}

	public void notInAJar(File file, int selectionCount) {
	}

	public void artifactDetermined(ArtifactData artifactData, int selectionCount) {
	}

	public void unknownDigest(FileAndDigest fileAndDigest, int selectionCount) {
	}

	public void notJavaElement(int selectionCount) {
	}


}
