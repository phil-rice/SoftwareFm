package org.softwareFm.card.api;

import org.eclipse.swt.widgets.Composite;

public interface ILineFactory {

	ILine make(Composite parent, KeyValue keyValue);

}
