package org.arc4eclipse.swtBinding.basic;

import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractBoundLabelAndText<Data extends IRepositoryDataItem> extends Composite implements IBound<Data> {
	protected final Text txtText;
	private final BindingContext<Data> context;
	private final String key;

	public AbstractBoundLabelAndText(Composite arg0, int arg1, String title, final BindingContext<Data> context, final String key) {
		super(arg0, arg1);
		this.context = context;
		this.key = key;
		setLayout(new FormLayout());

		Label lblTitle = new Label(this, SWT.NONE);
		FormData fd_lblTitle = new FormData();
		fd_lblTitle.left = new FormAttachment(0);
		lblTitle.setLayoutData(fd_lblTitle);
		lblTitle.setText(title == null ? "" : title);

		txtText = new Text(this, SWT.BORDER);
		fd_lblTitle.right = new FormAttachment(100);

		FormData fd_txtText = new FormData();
		fd_txtText.bottom = new FormAttachment(100);
		fd_txtText.right = new FormAttachment(100, 0);
		fd_txtText.top = new FormAttachment(0, 20);
		fd_txtText.left = new FormAttachment(0, 0);
		txtText.setLayoutData(fd_txtText);
		txtText.setText("");

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
	public void clear() {
		setText("");
	}

}
