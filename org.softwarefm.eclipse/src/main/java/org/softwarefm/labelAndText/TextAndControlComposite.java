package org.softwarefm.labelAndText;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.SoftwareFmComposite;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

abstract public class TextAndControlComposite<C> extends SoftwareFmComposite {

	private final StyledText text;

	public TextAndControlComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		text = new StyledText(getComposite(), SWT.READ_ONLY | SWT.WRAP | SWT.BORDER);
		makeComponent(container, getComposite());
		Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildrenWithLastGrabingVertical(getComposite());
		getComposite().setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		addListener(new ISelectedBindingListener() {

			public void notJavaElement(int selectionCount) {
				TextAndControlComposite.this.notJavaElement(selectionCount);
				layout();
			}

			public void unknownDigest(FileAndDigest fileAndDigest, int selectionCount) {
				TextAndControlComposite.this.unknownDigest(fileAndDigest, selectionCount);
				layout();
			}

			public void artifactDetermined(ArtifactData artifactData, int selectionCount) {
				TextAndControlComposite.this.artifactDetermined(artifactData, selectionCount);
				layout();
			}

			public void notInAJar(File file, int selectionCount) {
				TextAndControlComposite.this.notInAJar(file, selectionCount);
				layout();
			}

			public void digestDetermined(FileAndDigest fileAndDigest, int selectionCount) {
				TextAndControlComposite.this.digestDetermined(fileAndDigest, selectionCount);
				layout();
			}

			public void codeSelectionOccured(CodeData codeData, int selectionCount) {
				TextAndControlComposite.this.classAndMethodSelectionOccured(codeData, selectionCount);
				layout();

			}

			public boolean invalid() {
				return getComposite().isDisposed();
			}
			
			@Override
			public String toString() {
				return TextAndControlComposite.this.getClass().getSimpleName() + " Text: " + Strings.oneLine(getText());
			}
		});
	}

	abstract protected C makeComponent(SoftwareFmContainer<?> container, Composite parent);

	public void setText(String text) {
		this.text.setText(text);
		layout();
	}

	public void setMessage(String key, Object... args) {
		this.text.setText(IResourceGetter.Utils.getMessageOrException(resourceGetter, key, args));
	}

	public void appendText(String text) {
		this.text.append(text);
	}

	public void killLastLineAndappendText(String text) {
		String oldText = this.text.getText();
		int index = oldText.lastIndexOf('\n');
		if (index == -1)
			this.text.setText(text);
		else
			this.text.setText(oldText.substring(0, index + 1) + text);
	}

	public void classAndMethodSelectionOccured(CodeData codeData, int selectionCount) {
	}

	protected void notJavaElement(int selectionCount) {
	}

	public void digestDetermined(FileAndDigest fileAndDigest, int selectionCount) {
	}

	public void notInAJar(File file, int selectionCount) {
	}

	public void artifactDetermined(ArtifactData artifactData, int selectionCount) {
	}

	public void unknownDigest(FileAndDigest fileAndDigest, int selectionCount) {
	}

	public String getText() {
		return text.getText();
	}

}
