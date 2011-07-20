package org.arc4eclipse.swtBinding.basic;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseCallback;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.arc4eclipse.httpClient.response.IResponse;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BoundLabelAndText<Data extends IRepositoryDataItem> extends Composite implements IBound<Data> {

	private final Text txtText;
	private String currentUrl;
	private final BindingContext<Data> context;
	private final String key;

	public BoundLabelAndText(Composite arg0, int arg1, String title, final BindingContext<Data> context, final String key) {
		super(arg0, arg1);
		this.context = context;
		this.key = key;
		setLayout(new FormLayout());

		Label lblTitle = new Label(this, SWT.NONE);
		FormData fd_lblTitle = new FormData();
		fd_lblTitle.left = new FormAttachment(0, 5);
		lblTitle.setLayoutData(fd_lblTitle);
		lblTitle.setText(title == null ? "" : title);

		txtText = new Text(this, SWT.BORDER);
		fd_lblTitle.right = new FormAttachment(0, 90);
		fd_lblTitle.top = new FormAttachment(txtText, 0, SWT.TOP);
		FormData fd_txtText = new FormData();
		fd_txtText.bottom = new FormAttachment(100, 0);
		fd_txtText.right = new FormAttachment(100, 0);
		fd_txtText.top = new FormAttachment(0, 0);
		fd_txtText.left = new FormAttachment(0, 96);
		txtText.setLayoutData(fd_txtText);
		txtText.setText("");
		txtText.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (currentUrl != null) {
					IArc4EclipseCallback<Data> callback = new IArc4EclipseCallback<Data>() {
						@Override
						public void process(IResponse response, Data data) {
							System.out.println("Changed data to: " + data);
						}
					};
					context.repository.modifyData(currentUrl, key, getText(), context.mapper, callback);
				}
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
	}

	public void setText(String text) {
		txtText.setText(text);
	}

	public String getText() {
		return txtText.getText();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setData(Object arg0) {
		super.setData(arg0);
		if (arg0 != null && context.clazz.isAssignableFrom(arg0.getClass())) {
			Data data = (Data) arg0;
			Object value = data.get(key);
			setText(value == null ? "" : value.toString());
		}
	}

	@Override
	public void processResponse(IResponse response, Data data) {
		currentUrl = response.url();
		setData(data);
	}
}
