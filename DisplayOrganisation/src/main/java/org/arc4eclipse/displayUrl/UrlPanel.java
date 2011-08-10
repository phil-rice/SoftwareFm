package org.arc4eclipse.displayUrl;

import java.awt.Desktop;
import java.net.URI;

import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.strings.Urls;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class UrlPanel extends Composite {

	private final BoundTitleAndTextField urlField;
	private final ImageButton btnBrowse;

	public UrlPanel(Composite parent, int style, DisplayerContext context, DisplayerDetails details) {
		super(parent, style);
		urlField = new BoundTitleAndTextField(this, SWT.NULL, context, details);
		btnBrowse = urlField.addButton(context.imageFactory.makeImages(getDisplay()).getBrowseImage(), "Browse", new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				try {
					URI uri = Urls.withDefaultProtocol("http", urlField.getText());
					Desktop.getDesktop().browse(uri);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
		urlField.addHelpButton(details.help);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);

	}

	public void setValue(String url, String value) {
		urlField.setUrl(url);
		urlField.setText(value);
		ImageButton.Utils.setEnabledIfNotBlank(btnBrowse, value);

	}

}
