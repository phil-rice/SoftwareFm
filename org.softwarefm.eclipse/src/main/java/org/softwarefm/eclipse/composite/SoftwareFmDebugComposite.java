package org.softwarefm.eclipse.composite;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;

public class SoftwareFmDebugComposite extends SoftwareFmComposite {

	public SoftwareFmDebugComposite(Composite parent, final SoftwareFmContainer<?> container) {
		super(parent, container);
		getComposite().setLayout(new FillLayout());
		final StyledText text = new StyledText(getComposite(), SWT.WRAP | SWT.READ_ONLY);
		addListener(new ISelectedBindingListener() {

			public void notJavaElement(int selectionCount) {
				text.append("notJavaElement: (" + selectionCount + ")\n");
			}

			public void artifactDetermined(ArtifactData artifactData, int selectionCount) {
				text.append("projectDetermined: (" + selectionCount + ")\n" + artifactData + "\nUrl: " + container.urlStrategy.projectUrl(artifactData) + "\n");
			}

			public void notInAJar(File file, int selectionCount) {
				text.append("Not In A Jar: (" + selectionCount + ") file is: " + file + "\n");
			}

			public void digestDetermined(FileAndDigest fileAndDigest, int selectionCount) {
				text.append("Digest: (" + selectionCount + ") " + fileAndDigest + "\nUrl: " + container.urlStrategy.digestUrl(fileAndDigest.digest) + "\n");
			}

			public void codeSelectionOccured(CodeData codeData, int selectionCount) {
				text.setText("Class and method: (" + selectionCount + ")\n" + codeData + "\nUrl: " + container.urlStrategy.classAndMethodUrl(codeData) + "\n");
			}

			public void unknownDigest(FileAndDigest fileAndDigest, int selectionCount) {
				text.append("Unknown Digest: (" + selectionCount + ") " + fileAndDigest + "\n");
			}

			public boolean invalid() {
				return getComposite().isDisposed();
			}
		});
	}

}
