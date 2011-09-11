package org.softwareFm.swtBasics.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.functions.IFunction1;

public class TitleAndStyledTextField extends AbstractTitleAnd {

	private final StyledText txtText;

	public TitleAndStyledTextField(ConfigForTitleAnd config, Composite parent, String title) {
		super(config, parent, title, true);
		Composite content = getComposite();
		txtText = new StyledText(content, SWT.BORDER);
		txtText.setLayoutData(new RowData(400, 400));
		content.setLayout(new FormLayout());

		FormData lblLayoutData = new FormData();
		lblLayoutData.top = new FormAttachment(0, 0);
		lblLayoutData.left = new FormAttachment(0, 0);
		compTitleAndButtons.setLayoutData(lblLayoutData);

		FormData txtLayoutData = new FormData();
		txtLayoutData.top = new FormAttachment(0, 31);
		txtLayoutData.left = new FormAttachment(0, 0);
		txtLayoutData.bottom = new FormAttachment(100, 0);
		txtLayoutData.right = new FormAttachment(100, 0);
		txtText.setLayoutData(txtLayoutData);
		// Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
	}

	public void setText(final String text) {
		Swts.asyncExec(this, new Runnable() {
			@Override
			public void run() {
				txtText.setText(text);
			}
		});
	}

	public void appendText(final String text) {
		Swts.asyncExec(this, new Runnable() {
			@Override
			public void run() {
				txtText.append(text);
			}
		});
	}

	public String getText() {
		return txtText.getText();
	}

	public static void main(String[] args) {
		Swts.display("TitleAndStyledText", new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(Composite from) throws Exception {
				return new TitleAndStyledTextField(ConfigForTitleAnd.createForBasics(from.getDisplay()), from, "title").getComposite();
			}
		});
	}
}
