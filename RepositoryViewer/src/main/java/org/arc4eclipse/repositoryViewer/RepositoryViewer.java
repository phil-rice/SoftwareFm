package org.arc4eclipse.repositoryViewer;

import java.util.concurrent.Callable;

import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.repositoryViewer.constants.RepositoryViewerConstants;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.text.TitleAndStyledTextField;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class RepositoryViewer extends Composite {

	public RepositoryViewer(Composite arg0, int arg1) {
		super(arg0, arg1);
		setLayout(new FormLayout());

		final TitleAndTextField txtHost = new TitleAndTextField(this, SWT.NONE, "Host");
		FormData fd_host = new FormData();
		txtHost.setLayoutData(fd_host);
		txtHost.setText(RepositoryViewerConstants.defaultHost);

		final TitleAndTextField txtPort = new TitleAndTextField(this, SWT.NONE, "Port");
		fd_host.left = new FormAttachment(0, 10);
		fd_host.right = new FormAttachment(0, 172);
		fd_host.bottom = new FormAttachment(0, 31);
		fd_host.top = new FormAttachment(0, 10);
		FormData fd_post = new FormData();
		fd_post.top = new FormAttachment(0, 10);
		fd_post.left = new FormAttachment(0, 178);
		txtPort.setLayoutData(fd_post);
		txtPort.setText(Integer.toString(RepositoryViewerConstants.defaultPort));

		final TitleAndTextField txtUrl = new TitleAndTextField(this, SWT.NONE, "Url");
		FormData fd_url = new FormData();
		fd_url.bottom = new FormAttachment(0, 60);
		fd_url.top = new FormAttachment(0, 39);
		txtUrl.setLayoutData(fd_url);

		final TitleAndStyledTextField txtResponse = new TitleAndStyledTextField(this, SWT.NONE, "Response");
		fd_url.left = new FormAttachment(0, 8);
		fd_url.right = new FormAttachment(100, -12);
		FormData fd_response = new FormData();
		fd_response.bottom = new FormAttachment(100, -12);
		fd_response.right = new FormAttachment(100, -12);
		fd_response.top = new FormAttachment(0, 135);
		fd_response.left = new FormAttachment(0, 12);
		txtResponse.setLayoutData(fd_response);

		Callable<GetData> getData = new Callable<GetData>() {
			@Override
			public GetData call() throws Exception {
				String rawUrl = txtUrl.getText();
				String url = rawUrl.startsWith("/") ? rawUrl : "/" + rawUrl;
				return new GetData(txtHost.getText(), txtPort.getAsInteger(), url);
			}
		};
		IResponseCallback callback = new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				txtResponse.setText("Status: " + response.statusCode() + "\n " + response.asString());
			}
		};
		RepositoryGetButton htmlSubmit = new RepositoryGetButton(this, SWT.NONE, "html", getData, callback);
		FormData fd_htmlSubmit = new FormData();
		fd_htmlSubmit.left = new FormAttachment(0, 8);
		fd_htmlSubmit.top = new FormAttachment(0, 90);
		fd_htmlSubmit.bottom = new FormAttachment(0, 115);
		fd_htmlSubmit.right = new FormAttachment(0, 45);
		htmlSubmit.setLayoutData(fd_htmlSubmit);
		RepositoryGetButton jsonSubmit = new RepositoryGetButton(this, SWT.NONE, "json", getData, callback);
		FormData fd_jsonSubmit = new FormData();
		fd_jsonSubmit.left = new FormAttachment(0, 61);
		fd_jsonSubmit.top = new FormAttachment(0, 90);
		fd_jsonSubmit.bottom = new FormAttachment(0, 115);
		fd_jsonSubmit.right = new FormAttachment(0, 95);
		jsonSubmit.setLayoutData(fd_jsonSubmit);
		RepositoryGetButton xmlSubmit = new RepositoryGetButton(this, SWT.NONE, "xml", getData, callback);
		FormData fd_xmlSubmit = new FormData();
		fd_xmlSubmit.left = new FormAttachment(0, 109);
		fd_xmlSubmit.top = new FormAttachment(0, 90);
		fd_xmlSubmit.bottom = new FormAttachment(0, 115);
		fd_xmlSubmit.right = new FormAttachment(0, 140);
		xmlSubmit.setLayoutData(fd_xmlSubmit);

		final TitleAndTextField txtExtension = new TitleAndTextField(this, SWT.NONE, "Ext");
		FormData fd_extension = new FormData();
		fd_extension.bottom = new FormAttachment(0, 87);
		fd_extension.left = new FormAttachment(0, 8);
		fd_extension.right = new FormAttachment(txtHost, 0, SWT.RIGHT);
		fd_extension.top = new FormAttachment(0, 66);
		txtExtension.setLayoutData(fd_extension);
		txtExtension.setText(".json");
	}

	public static void main(String[] args) {
		Swts.display("Get", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				return new RepositoryViewer(from, SWT.NULL);
			}
		});
	}
}
