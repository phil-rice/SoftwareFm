package org.arc4eclipse.panel;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseLogger;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.httpClient.response.IResponse;
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

	public DebugPanel(Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style);
		setLayout(new FormLayout());
		textField = new TitleAndStyledTextField(this, SWT.NULL, "Messages");
		FormData fd_textField = new FormData();
		fd_textField.bottom = new FormAttachment(100, -12);
		fd_textField.top = new FormAttachment(0);
		fd_textField.right = new FormAttachment(100, -12);
		fd_textField.left = new FormAttachment(0);
		textField.setLayoutData(fd_textField);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void sendingRequest(String method, String url, Map<String, Object> parameters) {
		addToHistory(MessageFormat.format(" > {0} {1}   {2}\n", method, url, parameters));
	}

	@Override
	public void receivedReply(IResponse response, Object data) {
		addToHistory(MessageFormat.format("< {0} {1}   {2}", response.statusCode(), response.url(), data));
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
