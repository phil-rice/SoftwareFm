package org.softwareFm.display.displayer;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.smallButtons.ImageButtonConfig;
import org.softwareFm.display.smallButtons.SimpleImageButton;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CompressedStyledText extends AbstractCompressedText<StyledText> {


	public CompressedStyledText(Composite parent, int style, CompositeConfig config) {
		super(parent, style, config);
	}

	@Override
	protected Composite makeButtonAndTitlePanel(Composite parent) {
		return parent;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

	@Override
	protected void setLayout() {
		content.setLayout(Swts.getGridLayoutWithoutMargins(1));
		buttonPanel.setLayout(Swts.getHorizonalNoMarginRowLayout());
		text.setLayoutData(Swts.makeGrabHorizonalAndFillGridDataWithHeight(config.layout.styledTextHeight));
	}

	@Override
	protected StyledText makeTextControl(Composite parent, int style) {
		StyledText styledText = new StyledText(parent, style | SWT.VERTICAL);
		styledText.setEditable(false);
		styledText.setWordWrap(true);
		return styledText;
	}

	@Override
	protected void updateText() {
		text.setText(rawText);
	}

	public static void main(String[] args) {
		Swts.display(CompressedStyledText.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ImageRegistry imageRegistry = BasicImageRegisterConfigurator.forTests(from);
				IResourceGetter resourceGetter = IResourceGetter.Utils.noResources();
				CompositeConfig config = new CompositeConfig(from.getDisplay(), new SoftwareFmLayout(), imageRegistry, resourceGetter);
				CompressedStyledText compressedText = new CompressedStyledText(from, SWT.NULL, config);
				compressedText.setText("Here is some text");
				new SimpleImageButton(compressedText, ImageButtonConfig.forTests(imageRegistry).withImage(GeneralAnchor.sfmKey), false);
				return (Composite) compressedText.getControl();
			}
		});
	}

}
