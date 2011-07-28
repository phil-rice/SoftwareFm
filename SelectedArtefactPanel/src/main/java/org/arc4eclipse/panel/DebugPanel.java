package org.arc4eclipse.panel;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseLogger;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.text.TitleAndStyledTextField;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class DebugPanel extends Composite implements IArc4EclipseLogger {

	private static final int maxSize = 24;
	private final TitleAndStyledTextField textField;
	private final List<String> history = Lists.newList();
	private final TitleAndStyledTextField txtBinding;

	public DebugPanel(Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style);
		setLayout(new FormLayout());
		textField = new TitleAndStyledTextField(this, SWT.NULL, "Messages");
		FormData fd_textField = new FormData();
		fd_textField.bottom = new FormAttachment(100, -12);
		fd_textField.top = new FormAttachment(0, 12);
		fd_textField.right = new FormAttachment(0, 405);
		fd_textField.left = new FormAttachment(0);
		textField.setLayoutData(fd_textField);

		txtBinding = new TitleAndStyledTextField(this, SWT.NONE, "Binding Data");
		FormData fd_titleAndStyledTextField = new FormData();
		fd_titleAndStyledTextField.bottom = new FormAttachment(100, -12);
		fd_titleAndStyledTextField.right = new FormAttachment(0, 810);
		fd_titleAndStyledTextField.top = new FormAttachment(textField, 0, SWT.TOP);
		fd_titleAndStyledTextField.left = new FormAttachment(textField);
		txtBinding.setLayoutData(fd_titleAndStyledTextField);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setRipperData(BindingRipperResult result) {
		txtBinding.setText(result.toString());
	}

	@Override
	public void sendingRequest(String method, String url, Map<String, Object> parameters, Map<String, Object> context) {
		addToHistory(MessageFormat.format("> {0} {1}\nParams{2}\nContext {3}\n", method, url, parameters, context));
	}

	@Override
	public void receivedReply(IResponse response, Object data, Map<String, Object> context) {
		addToHistory(MessageFormat.format("< {0} {1}\nResponse{2}\nContext {3}\n", response.statusCode(), response.url(), data, context));
	}

	private void addToHistory(final String text) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				history.add(0, text);
				if (history.size() > maxSize)
					history.remove(maxSize);
				textField.setText(Strings.join(history, "\n"));
			}
		});
	}
}
