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
import org.softwareFm.display.swt.Swts.Grid;
import org.softwareFm.display.swt.Swts.Row;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CompressedStyledText extends AbstractCompressedText<StyledText> {

	public CompressedStyledText(Composite parent, int style, CompositeConfig config) {
		super(parent, style, config);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

	@Override
	protected void setLayout() {
		content.setLayout(Grid.getGridLayoutWithoutMargins(1));
		buttonPanel.setLayout(Row.getHorizonalNoMarginRowLayout());
		control.setLayoutData(Grid.makeGrabHorizonalAndFillGridDataWithHeight(config.layout.styledTextHeight));
	}

	@Override
	protected StyledText makeControl(Composite parent) {
		StyledText styledText = new StyledText(parent, SWT.VERTICAL);
		styledText.setEditable(false);
		styledText.setWordWrap(true);
		return styledText;
	}

	@Override
	protected void updateText() {
		control.setText(rawText);
	}

	public static void main(String[] args) {
		Show.display(CompressedStyledText.class.getSimpleName(), new IFunction1<Composite, Composite>() {
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

	@Override
	public void highlight() {
		Swts.setFontStyle(control, SWT.BOLD);
	}

	@Override
	public void unhighlight() {
		Swts.setFontStyle(control, SWT.NULL);
	}

}
