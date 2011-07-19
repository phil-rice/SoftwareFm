package org.arc4eclipse.shellPanel;

import org.arc4eclipse.httpClient.constants.HttpClientConstants;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ShellPanel extends Composite {
	private final TitleAndTextField textHost;
	private final TitleAndTextField textPort;
	private final TitleAndTextField textUserName;
	private final TitleAndTextField textPassword;
	private final TitleAndTextField textPrefix;
	private final TitleAndTextField textMiddle;
	private final TitleAndTextField textPostFix;
	private final TitleAndTextField textSending;
	private final Button btnGet;
	private final Button btnPost;
	private final StyledText responseText;

	public ShellPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());

		textHost = new TitleAndTextField(this, SWT.NONE, "Host");
		FormData fd_textHost = new FormData();
		fd_textHost.bottom = new FormAttachment(0, 31);
		fd_textHost.right = new FormAttachment(0, 369);
		fd_textHost.top = new FormAttachment(0, 10);
		fd_textHost.left = new FormAttachment(0, 10);
		textHost.setLayoutData(fd_textHost);
		textHost.setText(HttpClientConstants.defaultHost);

		textPort = new TitleAndTextField(this, SWT.NONE, "Port");
		FormData fd_textPort = new FormData();
		fd_textPort.bottom = new FormAttachment(0, 58);
		fd_textPort.right = new FormAttachment(0, 369);
		fd_textPort.top = new FormAttachment(0, 37);
		fd_textPort.left = new FormAttachment(0, 10);
		textPort.setLayoutData(fd_textPort);
		textPort.setText(Integer.toString(HttpClientConstants.defaultPort));

		textUserName = new TitleAndTextField(this, SWT.NONE, "Username");
		FormData fd_textUserName = new FormData();
		fd_textUserName.bottom = new FormAttachment(0, 91);
		fd_textUserName.right = new FormAttachment(0, 369);
		fd_textUserName.top = new FormAttachment(0, 70);
		fd_textUserName.left = new FormAttachment(0, 10);
		textUserName.setLayoutData(fd_textUserName);
		textUserName.setText("admin");

		textPassword = new TitleAndTextField(this, SWT.NONE, "Password");
		FormData fd_textPassword = new FormData();
		fd_textPassword.bottom = new FormAttachment(0, 121);
		fd_textPassword.right = new FormAttachment(0, 369);
		fd_textPassword.top = new FormAttachment(0, 100);
		fd_textPassword.left = new FormAttachment(0, 10);
		textPassword.setLayoutData(fd_textPassword);
		textPassword.setText("admin");

		textPrefix = new TitleAndTextField(this, SWT.NONE, "Prefix");
		FormData fd_textPrefix = new FormData();
		fd_textPrefix.bottom = new FormAttachment(0, 148);
		fd_textPrefix.right = new FormAttachment(0, 369);
		fd_textPrefix.top = new FormAttachment(0, 127);
		fd_textPrefix.left = new FormAttachment(0, 10);
		textPrefix.setLayoutData(fd_textPrefix);

		textMiddle = new TitleAndTextField(this, SWT.NONE, "Middle");
		FormData fd_textMiddle = new FormData();
		fd_textMiddle.bottom = new FormAttachment(0, 175);
		fd_textMiddle.right = new FormAttachment(0, 369);
		fd_textMiddle.top = new FormAttachment(0, 154);
		fd_textMiddle.left = new FormAttachment(0, 10);
		textMiddle.setLayoutData(fd_textMiddle);

		textPostFix = new TitleAndTextField(this, SWT.NONE, "Postfix");
		FormData fd_textPostFix = new FormData();
		fd_textPostFix.bottom = new FormAttachment(0, 206);
		fd_textPostFix.right = new FormAttachment(0, 369);
		fd_textPostFix.top = new FormAttachment(0, 185);
		fd_textPostFix.left = new FormAttachment(0, 10);
		textPostFix.setLayoutData(fd_textPostFix);
		textPostFix.setText(".json");

		textSending = new TitleAndTextField(this, SWT.NONE, "Sending");
		FormData fd_textSending = new FormData();
		fd_textSending.bottom = new FormAttachment(0, 233);
		fd_textSending.right = new FormAttachment(0, 369);
		fd_textSending.top = new FormAttachment(0, 212);
		fd_textSending.left = new FormAttachment(0, 10);
		textSending.setLayoutData(fd_textSending);

		btnGet = new Button(this, SWT.NONE);
		FormData fd_btnGet = new FormData();
		fd_btnGet.right = new FormAttachment(0, 85);
		fd_btnGet.top = new FormAttachment(0, 239);
		fd_btnGet.left = new FormAttachment(0, 10);
		btnGet.setLayoutData(fd_btnGet);
		btnGet.setText("Get");

		btnPost = new Button(this, SWT.NONE);
		FormData fd_btnPost = new FormData();
		fd_btnPost.right = new FormAttachment(0, 177);
		fd_btnPost.top = new FormAttachment(0, 239);
		fd_btnPost.left = new FormAttachment(0, 102);
		btnPost.setLayoutData(fd_btnPost);
		btnPost.setText("Post");
		addBuildSendingListen(textPrefix, textMiddle, textPostFix);
		btnGet.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// IRepositoryClientThin client = makeClient();
				// String request = textSending.getText();
				// System.out.println("Request: " + textHost.getText() + ":" + textPort.getText() + request);
				// Response response = client.get(request);
				// responseText.setText(response.asString());
			}

		});
		textSending.setText(getRequest());

		responseText = new StyledText(this, SWT.BORDER);
		FormData fd_styledText = new FormData();
		fd_styledText.bottom = new FormAttachment(100, -12);
		fd_styledText.right = new FormAttachment(100, -12);
		fd_styledText.top = new FormAttachment(btnGet);
		fd_styledText.left = new FormAttachment(0, 12);
		responseText.setLayoutData(fd_styledText);
	}

	// private IRepositoryClientThin makeClient() {
	// String host = textHost.getText();
	// int port = Integer.parseInt(textPort.getText());
	// String userName = textUserName.getText();
	// String password = textPassword.getText();
	// String baseUri = textPrefix.getText();
	// return new RepositoryClientThin(host, port, userName, password, baseUri);
	// }

	private String getRequest() {
		StringBuilder builder = new StringBuilder();
		builder.append(textPrefix.getText());
		builder.append(textMiddle.getText());
		builder.append(textPostFix.getText());
		String request = builder.toString();
		System.out.println("here: " + request);
		return request;
	}

	public void addBuildSendingListen(TitleAndTextField... texts) {
		ModifyListener listener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				String request = getRequest();
				textSending.setText(request);
			}

		};
		for (TitleAndTextField text : texts)
			text.addModifyListener(listener);
	}

	@Override
	protected void checkSubclass() {
	}

	public void setException(Exception e) {
		responseText.setText(e.getClass().getName() + "\n" + e.getMessage());
	}
}
