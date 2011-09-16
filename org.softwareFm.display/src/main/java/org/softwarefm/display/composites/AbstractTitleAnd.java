package org.softwarefm.display.composites;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.swtBasics.IHasComposite;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.display.SoftwareFmLayout;

public class AbstractTitleAnd implements IHasComposite, IButtonParent {

	protected final CompositeConfig config;
	protected final Composite compTitleAndButtons;
	protected final Label lblTitle;
	private final Composite compButtons;
	protected int buttonCount;
	private final Label lblFiller;
	private Composite content;

	public AbstractTitleAnd(CompositeConfig config, Composite parent, String titleOrTitleKey, boolean titleIsKey) {
		this.content = new Composite(parent, SWT.NULL);
		this.config = config;
		content.setLayout(Swts.getHorizonalNoMarginRowLayout());

		compTitleAndButtons = new Composite(content, SWT.NULL) {
			@Override
			public String toString() {
				return "compTitleAndButtons " + super.toString();
			}
		};
		SoftwareFmLayout layout = config.layout;
		int height = layout.titleHeight;
		compTitleAndButtons.setLayout(Swts.getHorizonalNoMarginRowLayout());
		compTitleAndButtons.setLayoutData(new RowData(layout.titleWidth + layout.buttonsWidth, height));

		String title = titleIsKey ? Resources.getOrException(config.resourceGetter,  titleOrTitleKey) : titleOrTitleKey;
		lblTitle = new Label(compTitleAndButtons, SWT.NULL);
		lblTitle.setLayoutData(new RowData(layout.titleWidth, height));
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
		int buttonsWidth = (config.layout.buttonSpacer + config.layout.smallButtonWidth)* buttonCount;
		int fillerWidth = config.layout.buttonsWidth - buttonsWidth-9;
		System.out.println("setLayoutData: buttons: " + buttonsWidth + ", filler: " + fillerWidth);
		compButtons.setLayoutData(new RowData(buttonsWidth, config.layout.smallButtonHeight));
		lblFiller.setLayoutData(new RowData(fillerWidth, config.layout.smallButtonHeight));
		compButtons.layout();
		compTitleAndButtons.layout();
		content.layout();
		Swts.layoutDump(this.getControl());
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

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}
}
