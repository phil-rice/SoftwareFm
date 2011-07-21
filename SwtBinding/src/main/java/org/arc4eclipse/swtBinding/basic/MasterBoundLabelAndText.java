package org.arc4eclipse.swtBinding.basic;

import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;

public class MasterBoundLabelAndText<Data extends IRepositoryDataItem> extends AbstractBoundLabelAndText<Data> {

	public MasterBoundLabelAndText(Composite arg0, int arg1, String title, final BindingContext<Data> context, final String key, final ICurrentUrlCalculator currentUrlCalculator) {
		super(arg0, arg1, title, context, key);
		txtText.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				String currentUrl = currentUrlCalculator == null ? null : currentUrlCalculator.url();
				if (currentUrl != null)
					context.repository.getData(currentUrl, context.clazz);
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
	}

	@Override
	public void processResponse(RepositoryDataItemStatus status, Data data) {
		switch (status) {
		case FOUND:
			setData(data);
			return;
		default:
			setText("");
		}
	};

}
