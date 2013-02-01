package org.softwarefm.core.labelAndText;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.composite.SoftwareFmComposite;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.ISelectedBindingListener;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.UsageStatData;
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
		addSelectedBindingListener(new ISelectedBindingListener() {

			public void notJavaElement(int selectionCount) {
				TextAndControlComposite.this.notJavaElement(selectionCount);
				layout();
			}

			public void unknownDigest(int selectionCount, FileAndDigest fileAndDigest) {
				TextAndControlComposite.this.unknownDigest(fileAndDigest, selectionCount);
				layout();
			}

			public void artifactDetermined(int selectionCount, ArtifactData artifactData) {
				TextAndControlComposite.this.artifactDetermined(artifactData, selectionCount);
				layout();
			}

			public void notInAJar(int selectionCount, File file) {
				TextAndControlComposite.this.notInAJar(file, selectionCount);
				layout();
			}

			public void digestDetermined(int selectionCount, FileAndDigest fileAndDigest) {
				TextAndControlComposite.this.digestDetermined(fileAndDigest, selectionCount);
				layout();
			}

			public void codeSelectionOccured(int selectionCount, CodeData codeData) {
				TextAndControlComposite.this.classAndMethodSelectionOccured(codeData, selectionCount);
				layout();

			}

			@Override
			public void friendsArtifactUsage(ArtifactData artifactData, Map<FriendData, UsageStatData> friendsUsage) {
				TextAndControlComposite.this.friendsArtifactUsage(artifactData, friendsUsage);
				layout();
			}

			@Override
			public void friendsCodeUsage(CodeData codeData, Map<FriendData, UsageStatData> friendsUsage) {
				TextAndControlComposite.this.friendsCodeUsage(codeData, friendsUsage);
				layout();
			}

			public boolean isValid() {
				return !getComposite().isDisposed();
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

	protected void friendsCodeUsage(CodeData codeData, Map<FriendData, UsageStatData> friendsUsage) {
	}

	protected void friendsArtifactUsage(ArtifactData artifactData, Map<FriendData, UsageStatData> friendsUsage) {

	}

	public String getText() {
		return text.getText();
	}

}
