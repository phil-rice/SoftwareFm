package org.softwareFm.swt.editors;

import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.internal.DataBodyComposite;
import org.softwareFm.swt.title.TitleSpec;
import org.softwareFm.swt.title.TitleWithTitlePaintListener;

abstract public class DataComposite<T extends Control> extends Composite implements IDataComposite<T> {
	private final CardConfig cardConfig;
	private final TitleSpec titleSpec;
	private final TitleWithTitlePaintListener titleWithTitlePaintListener;
	private final DataBodyComposite body;
	protected final IResourceGetter resourceGetter;

	public DataComposite(Composite parent, CardConfig cardConfig, String cardType, String title) {
		super(parent, SWT.NULL);
		this.cardConfig = cardConfig;
		this.titleSpec = Functions.call(cardConfig.titleSpecFn, ICardData.Utils.create(cardConfig, cardType, null, Collections.<String, Object> emptyMap()));
		titleWithTitlePaintListener = new TitleWithTitlePaintListener(this, cardConfig, titleSpec, title, "");
		body = new DataBodyComposite(this, cardConfig, titleSpec);
		resourceGetter = Functions.call(cardConfig.resourceGetterFn, null);
		
	}

	@Override
	public CardConfig getCardConfig() {
		return cardConfig;
	}

	@Override
	public TitleWithTitlePaintListener getTitle() {
		return titleWithTitlePaintListener;
	}

	@Override
	public TitleSpec getTitleSpec() {
		return titleSpec;
	}

	@Override
	public Composite getBody() {
		return body;
	}

	@Override
	public Composite getInnerBody() {
		return body.getInnerBody();
	}

	@Override
	public boolean useAllHeight() {
		return true;
	}

}
