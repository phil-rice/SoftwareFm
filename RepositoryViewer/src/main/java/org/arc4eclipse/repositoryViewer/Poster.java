package org.arc4eclipse.repositoryViewer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.arc4eclipse.repositoryViewer.constants.RepositoryViewerConstants;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.text.TitleAndStyledTextField;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class Poster extends Composite {

	public Poster(Composite arg0, int arg1) {
		super(arg0, arg1);
		setLayout(new FormLayout());

		final TitleAndTextField txtHost = new TitleAndTextField(this, SWT.NONE, "Host");
		txtHost.setText(RepositoryViewerConstants.defaultHost);
		FormData fd_host = new FormData();
		fd_host.right = new FormAttachment(0, 141);
		fd_host.bottom = new FormAttachment(0, 31);
		fd_host.top = new FormAttachment(0, 10);
		fd_host.left = new FormAttachment(0, 10);
		txtHost.setLayoutData(fd_host);

		final TitleAndTextField txtPort = new TitleAndTextField(this, SWT.NONE, "Port");
		txtPort.setText(Integer.toString(RepositoryViewerConstants.defaultPort));
		FormData fd_port = new FormData();
		fd_port.right = new FormAttachment(0, 288);
		fd_port.bottom = new FormAttachment(0, 31);
		fd_port.top = new FormAttachment(0, 10);
		fd_port.left = new FormAttachment(0, 157);
		txtPort.setLayoutData(fd_port);

		final TitleAndStyledTextField txtHeader = new TitleAndStyledTextField(this, SWT.NONE, "Headers");
		FormData fd_header = new FormData();
		fd_header.right = new FormAttachment(100, -10);
		fd_header.left = new FormAttachment(0, 10);
		fd_header.bottom = new FormAttachment(0, 140);
		fd_header.top = new FormAttachment(0, 50);
		txtHeader.setLayoutData(fd_header);
		txtHeader.setText("GET /content.html HTTP/1.0");

		Button btnSubmit = new Button(this, SWT.NONE);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.top = new FormAttachment(txtHeader, 6);
		fd_btnNewButton.left = new FormAttachment(txtHeader, 0, SWT.LEFT);
		btnSubmit.setLayoutData(fd_btnNewButton);
		btnSubmit.setText("Submit");

		final TitleAndStyledTextField txtResponse = new TitleAndStyledTextField(this, SWT.NONE, "Response");
		FormData fd_titleAndStyledTextField = new FormData();
		fd_titleAndStyledTextField.bottom = new FormAttachment(100, -15);
		fd_titleAndStyledTextField.right = new FormAttachment(100, -9);
		fd_titleAndStyledTextField.top = new FormAttachment(btnSubmit);
		fd_titleAndStyledTextField.left = new FormAttachment(btnSubmit, 0, SWT.LEFT);
		txtResponse.setLayoutData(fd_titleAndStyledTextField);

		btnSubmit.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				System.out.println("trying");
				try {
					Socket socket = new Socket(txtHost.getText(), txtPort.getAsInteger());
					try {
						PrintWriter writer = new PrintWriter(socket.getOutputStream());
						try {
							for (String string : txtHeader.getText().split("\n")) {
								writer.println(string);
							}
							writer.println();
							writer.println();
							writer.flush();
							BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							try {
								StringBuffer buffer = new StringBuffer();
								while (true) {
									String line = reader.readLine();
									if (line == null) {
										txtResponse.setText(buffer.toString());
										return;
									}
									buffer.append(line + "\n");
								}
							} finally {
								reader.close();
							}
						} finally {
							writer.close();
						}
					} finally {
						socket.close();
					}
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
		});

	}

	public static void main(String[] args) {
		Swts.display("Poster", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				return new Poster(from, SWT.NULL);
			}
		});
	}
}
