package org.arc4eclipse.swtBasics.text;

import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.Resources;
import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class AbstractTitleAnd extends Composite implements IButtonParent {

	protected final Composite compTitleAndButtons;
	protected final Label lblTitle;
	private final Composite compButtons;
	protected int buttonCount;
	private final Label lblFiller;
	protected final ConfigForTitleAnd config;

	public AbstractTitleAnd(ConfigForTitleAnd config, Composite parent, String titleKey) {
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

		String title = Resources.getTitle(config.resourceGetter, titleKey);
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

	public String getTitle() {
		return lblTitle.getText();
	}

	@Override
	public void buttonAdded() {
		buttonCount++;
		setLayoutData();
	}

	@Override
	public Composite getButtonComposte() {
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
