package org.arc4eclipse.swtBasics.text;

import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.utilities.exceptions.Exceptions;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class AbstractTitleAnd extends Composite {

	protected final Images images;
	protected final Composite compTitleAndButtons;
	protected final Label lblTitle;
	private final Composite compButtons;
	protected int buttonCount;
	private final Label lblFiller;
	private ImageButton helpButton;
	private String helpText;

	public AbstractTitleAnd(Composite parent, int style, IImageFactory imageFactory, String title) {
		super(parent, style);
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.justify = false;
		layout.pack = true;
		setLayout(layout);
		this.images = imageFactory.makeImages(getDisplay());

		compTitleAndButtons = new Composite(this, SWT.NULL);
		compTitleAndButtons.setLayout(Swts.getHorizonalNoMarginRowLayout());
		int height = 16;
		compTitleAndButtons.setLayoutData(new RowData(200, height));

		lblTitle = new Label(compTitleAndButtons, SWT.NULL);
		lblTitle.setLayoutData(new RowData(100, height));
		lblTitle.setText(title == null ? "" : title);

		lblFiller = new Label(compTitleAndButtons, SWT.NULL);

		compButtons = new Composite(compTitleAndButtons, SWT.NULL);
		compButtons.setLayout(Swts.getHorizonalNoMarginRowLayout());
		setLayoutData();
	}

	private void setLayoutData() {
		int buttonsWidth = 21 * buttonCount;
		int fillerWidth = 91 - buttonsWidth;
		compButtons.setLayoutData(new RowData(buttonsWidth, 22));
		lblFiller.setLayoutData(new RowData(fillerWidth, 22));
	}

	public ImageButton addButton(Image image, String tooltipText, final IImageButtonListener listener) {
		ImageButton button = new ImageButton(compButtons, image);
		// button.setSize(new Point(35, 12));
		button.addListener(new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				try {
					listener.buttonPressed(button);
				} catch (Exception e) {
					Status status = new Status(IStatus.ERROR, "My Plug-in ID", 0, Exceptions.stackTraceString(e.getStackTrace(), "\n"), null);
					ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Exception pressing button", e.getMessage(), status);
					throw WrappedException.wrap(e);
				}
			}
		});
		button.setTooltipText(tooltipText);
		RowData data = new RowData();
		data.height = 18;
		data.width = 18;
		button.setLayoutData(data);
		buttonCount++;
		setLayoutData();
		return button;
	}

	public void addHelpButton(String initialHelp) {
		helpButton = addButton(images.getHelpImage(), "Toggles the editable state of this value", new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				if (helpText != null && !"".equals(helpText))
					MessageDialog.openInformation(getShell(), "Help", helpText);
			}
		});
		setHelpText(initialHelp);
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
		helpButton.setTooltipText(helpText);
	}

	public String getTitle() {
		return lblTitle.getText();
	}
}
