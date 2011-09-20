package org.softwareFm.display.composites;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.swtBasics.IHasComposite;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.resources.IResourceGetter;

public  class TitleAnd implements IDisplayer, IHasComposite {

	protected final CompositeConfig config;
	protected final Label lblTitle;
	private final Composite compButtons;
	protected int buttonCount;
	private final Label lblFiller;
	private Composite content;
	private SoftwareFmLayout layout;

	public TitleAnd(CompositeConfig config, Composite parent, String titleOrTitleKey, boolean titleIsKey) {
		this.content = new Composite(parent, SWT.NULL);
		this.config = config;
		content.setLayout(Swts.getHorizonalNoMarginRowLayout());

		layout = config.layout;
		int height = layout.titleHeight;
		if (titleOrTitleKey == null)
			throw new NullPointerException();

		String title = titleIsKey ? Resources.getOrException(config.resourceGetter, titleOrTitleKey) : titleOrTitleKey;
		lblTitle = new Label(content, SWT.NULL);
		lblTitle.setLayoutData(new RowData(layout.titleWidth, height));
		lblTitle.setText(title == null ? "" : title);

		lblFiller = new Label(content, SWT.NULL);
		compButtons = new Composite(content, SWT.BORDER_DOT) {
			@Override
			public String toString() {
				return "compButtons " + super.toString();
			}
		};
		setLayoutData();
	}

	private void setLayoutData() {
		int buttonsWidth = (config.layout.buttonSpacer + config.layout.smallButtonWidth) * buttonCount ;
		int fillerWidth = config.layout.buttonsWidth - buttonsWidth ;
		compButtons.setLayoutData(new RowData(buttonsWidth, config.layout.displayerHeight));
		lblFiller.setLayoutData(new RowData(fillerWidth, config.layout.displayerHeight));
		GridLayout layout = new GridLayout(buttonCount, false);
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing=0;
		compButtons.setLayout(layout);
		compButtons.layout();
		content.layout();
	}

	@Override
	public void buttonAdded(IHasControl button) {
		buttonCount++;
		button.getControl().setLayoutData(new GridData(config.layout.smallButtonWidth, config.layout.smallButtonHeight));
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
	
	public String getTitle(){
		return lblTitle.getText();
	}
}
