package org.arc4eclipse.repositoryViewer;

import java.util.concurrent.Callable;

import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class RepositoryGetButton extends Button {

	public RepositoryGetButton(Composite arg0, int arg1, final String extension, final Callable<GetData> getData, final IResponseCallback callback) {
		super(arg0, arg1);
		setText(extension);
		addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					GetData data = getData.call();
					IHttpClient.Utils.builder(data.host, data.port).get(data.url + "." + extension).execute(callback);
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

	@Override
	protected void checkSubclass() {
	}
}
