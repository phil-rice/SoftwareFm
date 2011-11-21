package org.softwareFm.card.editors.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.card.internal.CardOutlinePaintListener;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.IMutableCardDataStore;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.title.Title;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.okCancel.OkCancel;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.resources.IResourceGetter;

abstract public class ValueEditorComposite<T extends Control> extends Composite implements IValueComposite<T> {

	public Title title;
	private final T editorControl;
	protected final OkCancel okCancel;
	protected final CardConfig cardConfig;
	protected final String originalValue;
	private final TitleSpec titleSpec;
	protected final Composite body;
	protected Composite innerBody;

	public ValueEditorComposite(Composite parent, int style, final CardConfig cardConfig, final String url, String cardType, final String key, Object initialValue, TitleSpec titleSpec, final IDetailsFactoryCallback callback) {
		super(parent, style);
		this.cardConfig = cardConfig;
		this.titleSpec = titleSpec;
		LineItem lineItem = new LineItem(cardType, key, initialValue);
		String name = Functions.call(cardConfig.nameFn, lineItem);
		title = new Title(this, cardConfig, titleSpec, name, url);
		body = new Composite(this, SWT.NULL) {
			@Override
			public Rectangle getClientArea() {
				// note that the topMargin doesn't reference this component: it affects the space between the top of somewhere and the title.
				// There is a two pixel gap between the top of the card and the title
				Rectangle clientArea = super.getClientArea();
				int cardWidth = clientArea.width - cardConfig.rightMargin - cardConfig.leftMargin;
				int cardHeight = clientArea.height - cardConfig.bottomMargin - 2;
				Rectangle result = new Rectangle(clientArea.x + cardConfig.leftMargin, clientArea.y + 2, cardWidth, cardHeight);
				return result;
			}
		};// needed to allow the CardOutlinePaintListener to paint around
			// setBackground(titleSpec.background);
		innerBody = new Composite(body, SWT.NULL);

		originalValue = Functions.call(cardConfig.valueFn, lineItem);
		editorControl = makeEditorControl(innerBody, originalValue);
		IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, null);
		okCancel = new OkCancel(innerBody, resourceGetter, new Runnable() {
			@Override
			public void run() {
				IMutableCardDataStore cardDataStore = (IMutableCardDataStore) cardConfig.cardDataStore;
				String value = getValue();
				if (!value.equals(originalValue))
					callback.updateDataStore(cardDataStore, url, key, value);
				ValueEditorComposite.this.dispose();
			}
		}, new Runnable() {
			@Override
			public void run() {
				ValueEditorComposite.this.dispose();
			}
		});
		addAnyMoreButtons();
		
		updateEnabledStatusOfButtons();
		body.addPaintListener(new CardOutlinePaintListener(titleSpec, cardConfig));
		editorControl.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE)
					okCancel.cancel();
			}
		});
		innerBody.setBackground(titleSpec.background);
	}

	protected void addAnyMoreButtons() {
	}

	abstract protected T makeEditorControl(Composite parent, String originalValue);

	abstract protected String getValue();

	abstract protected void updateEnabledStatusOfButtons();


	@Override
	public CardConfig getCardConfig() {
		return cardConfig;
	}

	@Override
	public Title getTitle() {
		return title;
	}

	@Override
	public Composite getBody() {
		return body;
	}

	@Override
	public Composite getInnerBody() {
		return innerBody;
	}

	@Override
	public T getEditor() {
		return editorControl;
	}

	@Override
	public OkCancel getOkCancel() {
		return okCancel;
	}

	public TitleSpec getTitleSpec() {
		return titleSpec;
	}

}