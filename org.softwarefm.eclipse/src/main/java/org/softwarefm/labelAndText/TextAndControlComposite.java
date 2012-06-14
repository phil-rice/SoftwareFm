package org.softwarefm.labelAndText;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.SoftwareFmComposite;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.utilities.resources.IResourceGetter;

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

			public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				TextAndControlComposite.this.unknownDigest(fileNameAndDigest, selectionCount);
				layout();
			}

			public void projectDetermined(ProjectData projectData, int selectionCount) {
				TextAndControlComposite.this.projectDetermined(projectData, selectionCount);
				layout();
			}

			public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				TextAndControlComposite.this.notInAJar(fileNameAndDigest, selectionCount);
				layout();
			}

			public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				TextAndControlComposite.this.digestDetermined(fileNameAndDigest, selectionCount);
				layout();
			}

			public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
				TextAndControlComposite.this.classAndMethodSelectionOccured(expressionData, selectionCount);
				layout();

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

	public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
	}

	protected void notJavaElement(int selectionCount) {
	}

	public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
	}

	public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
	}

	public void projectDetermined(ProjectData projectData, int selectionCount) {
	}

	public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
	}

	public String getText() {
		return text.getText();
	}

}
