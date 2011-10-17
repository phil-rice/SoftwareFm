package org.softwareFm.card.api;

import org.eclipse.swt.widgets.Composite;

public interface IDetailFactory {

	Composite makeDetail(Composite parent, int style, ICardFactory cardFactory, ICardSelectedListener listener, String url, KeyValue keyValue);
}
