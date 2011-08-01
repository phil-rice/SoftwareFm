package org.arc4eclipse.swtBasics.text;

import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.images.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class AbstractTitleAnd extends Composite {

	private final Images images;
	private final Composite compForTitle;
	private final Label lblTitle;

	public AbstractTitleAnd(Composite parent, int style, Images images, String title) {
		super(parent, style);
		this.images = images;
		setLayout(new FormLayout());

		compForTitle = new Composite(this, SWT.NULL);
		FormData fd_compButtons = new FormData();
		fd_compButtons.left = new FormAttachment(0, 0);
		fd_compButtons.right = new FormAttachment(100, 0);
		compForTitle.setLayoutData(fd_compButtons);
		RowLayout layout = new RowLayout();
		compForTitle.setLayout(layout);
		layout.marginTop = 0;
		layout.marginBottom = 0;

		lblTitle = new Label(compForTitle, SWT.NONE);
		lblTitle.setText(title == null ? "" : title);

	}

	public ImageButton addButton(Image image, String tooltipText, IImageButtonListener listener) {
		ImageButton button = new ImageButton(compForTitle, image);
		// button.setSize(new Point(35, 12));
		button.addListener(listener);
		button.setTooltipText(tooltipText);
		RowData data = new RowData();
		data.height = 18;
		button.setLayoutData(data);
		return button;
	}

	public String getTitle() {
		return lblTitle.getText();
	}
}
