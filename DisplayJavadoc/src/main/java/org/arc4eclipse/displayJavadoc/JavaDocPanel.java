package org.arc4eclipse.displayJavadoc;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.panel.ISelectedBindingManager;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class JavaDocPanel extends Composite {

	private final BoundTitleAndTextField textField;

	public JavaDocPanel(Composite parent, int style, DisplayerContext context, NameSpaceAndName nameSpaceAndName, String title) {
		super(parent, style);
		setLayout(new GridLayout());
		textField = new BoundTitleAndTextField(this, context, nameSpaceAndName, title);
		Image linkImage = context.imageFactory.makeImages(getDisplay()).getLinkImage();
		ImageButton btnAttach = textField.addButton(linkImage, "Attach", new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				System.out.println("Attaching javadoc");
			}
		});
		btnAttach.setEnabled(!textField.getText().equals(""));
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
	}

	public void setValue(String url, String value) {
		textField.setUrl(url);
		textField.setText(value);
	}

	public static void main(String[] args) {

		final IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();
		Swts.display("Javadoc", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				DisplayerContext context = new DisplayerContext(IImageFactory.Utils.imageFactory(), ISelectedBindingManager.Utils.noSelectedBindingManager(), repository);
				final JavaDocPanel panel = new JavaDocPanel(from, SWT.BORDER, context, NameSpaceAndName.Utils.rip("text_some"), "Javadoc");
				from.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						panel.setValue("url", "some value");
					}
				});
				return panel;
			}
		});
		repository.shutdown();
	}
}
