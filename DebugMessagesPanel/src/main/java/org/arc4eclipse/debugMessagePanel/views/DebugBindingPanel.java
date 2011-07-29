package org.arc4eclipse.debugMessagePanel.views;

import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.panel.ISelectedBindingListener;
import org.arc4eclipse.panel.ISelectedBindingManager;
import org.arc4eclipse.swtBasics.text.TitleAndStyledTextField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class DebugBindingPanel extends Composite implements ISelectedBindingListener {
	private final TitleAndStyledTextField titleAndStyledTextField;

	public DebugBindingPanel(Composite parent, int style, ISelectedBindingManager selectedBindingManager) {
		super(parent, style);
		setLayout(new FormLayout());

		titleAndStyledTextField = new TitleAndStyledTextField(this, SWT.NONE, "Binding");
		FormData fd_titleAndStyledTextField = new FormData();
		fd_titleAndStyledTextField.bottom = new FormAttachment(100, 0);
		fd_titleAndStyledTextField.right = new FormAttachment(100, 0);
		fd_titleAndStyledTextField.top = new FormAttachment(0, 0);
		fd_titleAndStyledTextField.left = new FormAttachment(0, 0);
		titleAndStyledTextField.setLayoutData(fd_titleAndStyledTextField);
		selectedBindingManager.addSelectedArtifactSelectionListener(this);
	}

	@Override
	public void selectionOccured(BindingRipperResult ripperResult) {
		titleAndStyledTextField.setText(ripperResult.toString());
	}

}
