package org.arc4eclipse.swtBinding.basic;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseCallback;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.arc4eclipse.httpClient.response.IResponse;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;

public class BoundLabelAndText<Data extends IRepositoryDataItem> extends AbstractBoundLabelAndText<Data> {

	public BoundLabelAndText(Composite arg0, int arg1, String title, final BindingContext<Data> context, final String key, final ICurrentUrlCalculator currentUrlCalculator) {
		super(arg0, arg1, title, context, key);
		txtText.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				String currentUrl = currentUrlCalculator == null ? null : currentUrlCalculator.url();
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

}
