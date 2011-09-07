package org.softwareFm.swtBasics.text;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.resources.IResourceGetter;

public class AbstractTitleAnd extends Composite implements IButtonParent {

	protected final Composite compTitleAndButtons;
	protected final Label lblTitle;
	private final Composite compButtons;
	protected int buttonCount;
	private final Label lblFiller;
	protected final ConfigForTitleAnd config;

	public AbstractTitleAnd(ConfigForTitleAnd config, Composite parent, String titleOrTitleKey, boolean titleIsKey) {
		super(parent, config.style);
		this.config = config;
		setLayout(Swts.getHorizonalNoMarginRowLayout());

		compTitleAndButtons = new Composite(this, SWT.NULL) {
			@Override
			public String toString() {
				return "compTitleAndButtons " + super.toString();
			}
		};
		int height = config.titleHeight;
		compTitleAndButtons.setLayout(Swts.getHorizonalNoMarginRowLayout());
		compTitleAndButtons.setLayoutData(new RowData(config.titleWidth + config.buttonsWidth, height));

		String title = titleIsKey ? Resources.getTitle(config.resourceGetter, titleOrTitleKey) : titleOrTitleKey;
		lblTitle = new Label(compTitleAndButtons, SWT.NULL);
		lblTitle.setLayoutData(new RowData(config.titleWidth, height));
		lblTitle.setText(title == null ? "" : title);

		lblFiller = new Label(compTitleAndButtons, SWT.NULL);
		compButtons = new Composite(compTitleAndButtons, SWT.BORDER_DOT) {
			@Override
			public String toString() {
				return "compButtons " + super.toString();
			}
		};
		compButtons.setLayout(Swts.getHorizonalNoMarginRowLayout());
		setLayoutData();
	}

	private void setLayoutData() {
		int buttonsWidth = config.buttonSpacer * buttonCount;
		int fillerWidth = config.buttonsWidth - 22 - buttonsWidth;
		System.out.println("setLayoutData: buttons: " + buttonsWidth + ", filler: " + fillerWidth);
		compButtons.setLayoutData(new RowData(buttonsWidth, config.buttonHeight));
		lblFiller.setLayoutData(new RowData(fillerWidth, config.buttonHeight));
		compButtons.layout();
		compTitleAndButtons.layout();
		layout();
	}

	public String getTitle() {
		return lblTitle.getText();
	}

	@Override
	public void buttonAdded() {
		buttonCount++;
		setLayoutData();
	}

	@Override
	public Composite getButtonComposite() {
		return compButtons;
	}

	@Override
	public ImageRegistry getImageRegistry() {
		return config.imageRegistry;
	}

	@Override
	public IResourceGetter getResourceGetter() {
		return config.resourceGetter;
	}
}
