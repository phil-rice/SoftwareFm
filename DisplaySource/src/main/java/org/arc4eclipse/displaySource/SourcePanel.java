package org.arc4eclipse.displaySource;

import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class SourcePanel extends Composite {

	private final ImageButton btnAttach;
	private final TitleAndTextField txtLocal;
	private final BoundTitleAndTextField txtRepository;
	private BindingRipperResult ripped;
	private String value;

	public SourcePanel(Composite parent, int style, DisplayerContext context, DisplayerDetails displayerDetails) {
		super(parent, style);
		setLayout(new GridLayout());
		txtRepository = new BoundTitleAndTextField(this, SWT.NULL, context, displayerDetails);
		btnAttach = txtRepository.addButton(context.imageFactory.makeImages(getDisplay()).getLinkImage(), "Attach", new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				if (ripped != null) {
					System.out.println("Linking");
					try {
						ripped.packageFragment.attachSource(new Path(value), null, new NullProgressMonitor());
					} catch (JavaModelException e) {
						throw WrappedException.wrap(e);
					}
				}
			}
		});
		if (displayerDetails.help != null)
			txtRepository.addHelpButton(displayerDetails.help);
		txtLocal = new TitleAndTextField(this, SWT.NULL, "Current setting", false);
		txtLocal.addHelpButton(displayerDetails.help);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
	}

	public void setValue(String url, BindingRipperResult ripped, String value) {
		this.ripped = ripped;
		this.value = value;
		txtRepository.setUrl(url);
		txtRepository.setText(value);
		txtLocal.setText(Strings.nullSafeToString(ripped == null ? null : ripped.sourceAttachmentPath));
		ImageButton.Utils.setEnabledIfNotBlank(btnAttach, value);
	}

}
