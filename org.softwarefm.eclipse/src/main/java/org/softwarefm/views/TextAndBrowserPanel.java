package org.softwarefm.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.swt.Swts;

abstract public class TextAndBrowserPanel extends SoftwareFmPanel {

	private final StyledText text;
	private final Browser browser;
	private String url;

	public TextAndBrowserPanel(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		text = new StyledText(getComposite(), SWT.READ_ONLY | SWT.WRAP | SWT.BORDER);
		browser = new Browser(getComposite(), SWT.NULL);
		Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildrenWithLastGrabingVertical(getComposite());

		addListener(new ISelectedBindingListener() {
			@Override
			public void notJavaElement(int selectionCount) {
				TextAndBrowserPanel.this.notJavaElement(selectionCount);
				clearBrowser();
				layout();
			}

			@Override
			public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				TextAndBrowserPanel.this.unknownDigest(fileNameAndDigest, selectionCount);
				layout();
			}

			@Override
			public void projectDetermined(ProjectData projectData, int selectionCount) {
				TextAndBrowserPanel.this.projectDetermined(projectData, selectionCount);
				layout();
			}

			@Override
			public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				TextAndBrowserPanel.this.notInAJar(fileNameAndDigest, selectionCount);
				layout();
			}

			@Override
			public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				TextAndBrowserPanel.this.digestDetermined(fileNameAndDigest, selectionCount);
				layout();
			}

			@Override
			public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
				TextAndBrowserPanel.this.classAndMethodSelectionOccured(expressionData, selectionCount);
				layout();

			}
		});
	}

	public void setText(String text) {
		this.text.setText(text);
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

	public void setUrl(String url) {
		this.url = url;
		this.browser.setUrl(url);
	}

	public String getUrl() {
		return url;
	}

	public void clearBrowser() {
		this.browser.setText("");
		this.url = "";
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
