package org.arc4eclipse.panel;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.swtBinding.basic.BindingContext;
import org.arc4eclipse.swtBinding.basic.IBound;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractRepositoryDataPanel<T extends IRepositoryDataItem> extends Composite implements IRepositoryDataPanel<T> {
	protected final BindingContext<T> context;

	public AbstractRepositoryDataPanel(Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style);
		setLayout(new FormLayout());
		context = new BindingContext<T>(getDataClass(), repository, mapper());
	}

	@Override
	public void process(IResponse response, T data) {
		if (data == null)
			clearBoundChildren();
		else
			for (IBound<T> bound : boundChildren())
				bound.processResponse(response, data);
	}

	public void clearBoundChildren() {
		for (IBound<T> bound : boundChildren())
			bound.clear();
	};
}
