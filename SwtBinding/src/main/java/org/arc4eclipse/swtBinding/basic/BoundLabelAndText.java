package org.arc4eclipse.swtBinding.basic;

import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

public class BoundLabelAndText<Data extends IRepositoryDataItem> extends AbstractBoundLabelAndText<Data> {

	public BoundLabelAndText(Composite composite, int arg1, String title, final BindingContext<Data> context, final String key, final ICurrentUrlCalculator currentUrlCalculator) {
		super(composite, arg1, title, context, key);
		FormData formData = (FormData) txtText.getLayoutData();
		formData.top = new FormAttachment(0, 20);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		formData.bottom = new FormAttachment(100);

		txtText.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent composite) {
				getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						String currentUrl = currentUrlCalculator == null ? null : currentUrlCalculator.url();
						if (currentUrl != null)
							context.repository.modifyData(currentUrl, key, getText(), context.clazz);
					}
				});
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
	}

	@Override
	public void processResponse(final RepositoryDataItemStatus status, final Data data) {
		switch (status) {
		case FOUND:
			setData(data);
			return;
		default:
			setText("");
		}

	};

}
