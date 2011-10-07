package org.softwareFm.display.displayer;

import java.text.MessageFormat;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.smallButtons.ImageButtonConfig;
import org.softwareFm.display.smallButtons.SimpleImageButton;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CompressedText extends AbstractCompressedText<Label> {

	public CompressedText(Composite parent, int style, CompositeConfig config) {
		super(parent, style, config);
	}

	@Override
	protected void setLayout() {
		content.setLayout(Swts.getGridLayoutWithoutMargins(2));
		buttonPanel.setLayout(Swts.getHorizonalNoMarginRowLayout());
		text.setLayoutData(Swts.makeGrabHorizonalAndFillGridData());
	}

	@Override
	protected void updateText() {
		text.setText(MessageFormat.format("{0}: {1}", title, rawText));
	}

	@Override
	protected Label makeTextControl(Composite parent, int style) {
		return new Label(parent, style);
	}

	public static void main(String[] args) {
		Swts.display(CompressedText.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ImageRegistry imageRegistry = BasicImageRegisterConfigurator.forTests(from);
				IResourceGetter resourceGetter = IResourceGetter.Utils.noResources();
				CompositeConfig config = new CompositeConfig(from.getDisplay(), new SoftwareFmLayout(), imageRegistry, resourceGetter);
				CompressedText compressedText = new CompressedText(from, SWT.NULL, config);
				compressedText.setText("Here is some text");
				new SimpleImageButton(compressedText, ImageButtonConfig.forTests(imageRegistry).withImage(GeneralAnchor.sfmKey), false);
				return (Composite) compressedText.getControl();
			}
		});
	}

	@Override
	protected Composite makeButtonAndTitlePanel(Composite parent) {
		return parent;
	}

}
