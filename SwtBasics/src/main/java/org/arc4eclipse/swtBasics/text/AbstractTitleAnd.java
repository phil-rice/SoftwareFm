package org.arc4eclipse.swtBasics.text;

import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.images.Resources;
import org.arc4eclipse.utilities.exceptions.Exceptions;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class AbstractTitleAnd extends Composite {

	protected final Composite compTitleAndButtons;
	protected final Label lblTitle;
	private final Composite compButtons;
	protected int buttonCount;
	private final Label lblFiller;
	private ImageButton helpButton;
	private String helpText;
	protected final ConfigForTitleAnd config;

	public AbstractTitleAnd(ConfigForTitleAnd config, Composite parent, String title) {
		super(parent, config.style);
		this.config = config;

		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.justify = false;
		layout.pack = true;
		setLayout(layout);

		compTitleAndButtons = new Composite(this, SWT.NULL);
		compTitleAndButtons.setLayout(Swts.getHorizonalNoMarginRowLayout());
		int height = config.titleHeight;
		compTitleAndButtons.setLayoutData(new RowData(config.titleWidth + config.buttonsWidth, height));

		lblTitle = new Label(compTitleAndButtons, SWT.NULL);
		lblTitle.setLayoutData(new RowData(config.titleWidth, height));
		lblTitle.setText(title == null ? "" : title);

		lblFiller = new Label(compTitleAndButtons, SWT.NULL);

		compButtons = new Composite(compTitleAndButtons, SWT.NULL);
		compButtons.setLayout(Swts.getHorizonalNoMarginRowLayout());
		setLayoutData();
	}

	private void setLayoutData() {
		int buttonsWidth = config.buttonSpacer * buttonCount;
		int fillerWidth = config.buttonsWidth - 9 - buttonsWidth;
		compButtons.setLayoutData(new RowData(buttonsWidth, config.buttonHeight));
		lblFiller.setLayoutData(new RowData(fillerWidth, config.buttonHeight));
	}

	public ImageButton addButton(String imageKey, String tooltipKey, final IImageButtonListener listener) {
		ImageButton button = new ImageButton(compButtons, config.imageRegistry, imageKey, false);
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
		button.setTooltipText(Resources.getTooltip(config.resourceGetter, tooltipKey));
		RowData data = new RowData();
		data.height = 18;
		data.width = 18;
		button.setLayoutData(data);
		buttonCount++;
		setLayoutData();
		return button;
	}

	public void addHelpButton(final String helpKey) {
		helpButton = addButton(SwtBasicConstants.helpKey, helpKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				if (helpText != null && !"".equals(helpText))
					MessageDialog.openInformation(getShell(), "Help", Resources.getDetailedHelp(config.resourceGetter, helpKey));
			}
		});
		setHelpText(helpKey);
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
		if (helpButton != null)
			helpButton.setTooltipText(Strings.nullSafeToString(helpText));
	}

	public String getTitle() {
		return lblTitle.getText();
	}
}
