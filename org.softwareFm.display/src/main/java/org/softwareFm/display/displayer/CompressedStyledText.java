package org.softwareFm.display.displayer;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
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
	protected void setLayout() {
		content.setLayout(Swts.getGridLayoutWithoutMargins(1));
		buttonPanel.setLayout(Swts.getHorizonalNoMarginRowLayout());
		GridData gridData = Swts.makeGrabHorizonalAndFillGridData();
		gridData.heightHint = config.layout.styledTextHeight;
		text.setLayoutData(gridData);
	}

	@Override
	protected StyledText makeTextControl(Composite parent, int style) {
		StyledText styledText = new StyledText(parent, style|SWT.VERTICAL);
		styledText.setEditable(false);
		styledText.setWordWrap(true);
		return styledText;
	}

	@Override
	public void setText(String text) {
		this.text.setText(text);
	}

	@Override
	public String getText() {
		return text.getText();
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
