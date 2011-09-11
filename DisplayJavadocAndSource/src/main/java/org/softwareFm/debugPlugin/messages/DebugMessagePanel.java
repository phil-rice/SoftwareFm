package org.softwareFm.debugPlugin.messages;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.debugPlugin.DebugPanelConstants;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repository.api.ISoftwareFmLogger;
import org.softwareFm.repository.api.ISoftwareFmRepository;
import org.softwareFm.repositoryFacardConstants.RepositoryFacardConstants;
import org.softwareFm.softwareFmImages.ImageButtons;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.swtBasics.images.IImageButtonListener;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.TitleAndStyledTextField;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.strings.Strings;

public class DebugMessagePanel extends Composite implements ISoftwareFmLogger {
	private final int logSize = 8;
	private final List<String> log = Collections.synchronizedList(Lists.<String> newList());
	private final TitleAndStyledTextField titleAndStyledTextField;

	public DebugMessagePanel(Composite parent, int style, ConfigForTitleAnd config, ISoftwareFmRepository repository) {
		super(parent, style);
		setLayout(new FormLayout());

		titleAndStyledTextField = new TitleAndStyledTextField(config, this, DebugPanelConstants.messagesKey);
		FormData fd_titleAndStyledTextField = new FormData();
		fd_titleAndStyledTextField.bottom = new FormAttachment(100, 0);
		fd_titleAndStyledTextField.right = new FormAttachment(100, 0);
		fd_titleAndStyledTextField.top = new FormAttachment(0, 0);
		fd_titleAndStyledTextField.left = new FormAttachment(0, 0);
		titleAndStyledTextField.getControl().setLayoutData(fd_titleAndStyledTextField);
		repository.addLogger(this);
		ImageButtons.addRowButton(titleAndStyledTextField, GeneralAnchor.clearKey, DebugPanelConstants.messageClearKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				log.clear();
				titleAndStyledTextField.setText("");
			}
		});
	}

	@Override
	public void sendingRequest(String method, String url, Map<String, Object> parameters, Map<String, Object> context) {
		log("> {0} {1}\nParameters: {2}\nContext: {3}", method, url, parameters, context);
	}

	@Override
	public void receivedReply(IResponse response, Object data, Map<String, Object> context) {
		int statusCode = response.statusCode();
		String responseString = RepositoryFacardConstants.okStatusCodes.contains(statusCode) ? "<Ok>" : response.asString();
		log("< {0} {1}\n{2}\nData: {3}\nContext: {4}", statusCode, response.url(), responseString, data, context);
	}

	private void log(final String pattern, final Object... arguments) {
		if (!isDisposed())
			getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					String message = MessageFormat.format(pattern, arguments);
					String text = Strings.addToRollingLog(log, logSize, "\n", message);
					titleAndStyledTextField.setText(text);
				}
			});
	}

}
