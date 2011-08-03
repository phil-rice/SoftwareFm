package org.arc4eclipse.displayJavadoc;

import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class JavadocPanel extends Composite {

	private final ImageButton btnAttach;
	private final TitleAndTextField txtLocal;
	private final BoundTitleAndTextField txtRepository;

	public JavadocPanel(Composite parent, int style, DisplayerContext context, NameSpaceAndName nameSpaceAndName, String title) {
		super(parent, style);
		setLayout(new GridLayout());
		txtRepository = new BoundTitleAndTextField(this, SWT.NULL, context, nameSpaceAndName, title);
		btnAttach = txtRepository.addButton(context.imageFactory.makeImages(getDisplay()).getLinkImage(), "Attach", new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				System.out.println("Linking");
			}
		});
		txtRepository.addHelpButton(DisplayJavadocConstants.helpValueInRepository);

		txtLocal = new TitleAndTextField(this, SWT.NULL, context.imageFactory, "Current setting", false);
		txtLocal.addHelpButton(DisplayJavadocConstants.helpCurrentValue);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
	}

	public void setValue(String url, BindingRipperResult ripped, String value) {
		txtRepository.setUrl(url);
		txtRepository.setText(value);
		txtLocal.setText(value);
		btnAttach.setEnabled(!"".equals(value));
	}

}
