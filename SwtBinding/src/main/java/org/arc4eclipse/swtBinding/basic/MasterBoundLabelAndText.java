package org.arc4eclipse.swtBinding.basic;

import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.eclipse.swt.widgets.Composite;

public class MasterBoundLabelAndText<Data extends IRepositoryDataItem> extends AbstractBoundLabelAndText<Data> {

	public MasterBoundLabelAndText(Composite arg0, int arg1, String title, BindingContext<Data> context, String key) {
		super(arg0, arg1, title, context, key);
	}
}
