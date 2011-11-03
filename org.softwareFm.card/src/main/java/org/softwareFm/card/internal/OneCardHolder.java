package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.display.composites.IHasComposite;

public class OneCardHolder implements IHasComposite{

	private final HoldsCardHolder content;

	public OneCardHolder(Composite parent, CardConfig cardConfig, String url, String title, ICardSelectedListener listener){
		content = new HoldsCardHolder(parent, SWT.NULL, cardConfig);
		content.makeCardHolder(url, title);
		content.addCardSelectedListener(listener);
		content.setLayout(new FillWithAspectRatioLayoutManager(cardConfig.defaultWidthWeight, cardConfig.defaultHeightWeight));
	}
	
	
	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

}
