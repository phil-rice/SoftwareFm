package org.softwareFm.card.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.IMutableCardDataStore;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.internal.CardOutlinePaintListener;
import org.softwareFm.card.internal.details.IDetailsFactoryCallback;
import org.softwareFm.card.internal.details.TitleSpec;
import org.softwareFm.card.internal.title.Title;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.maps.Maps;

abstract class ValueEditorComposite<T extends Control> extends Composite {

	public Title titleLabel;
	final T editorControl;
	protected final OkCancel okCancel;
	protected final CardConfig cardConfig;
	protected final String originalValue;
	private final Composite body;

	public ValueEditorComposite(Composite parent, int style, final CardConfig cardConfig, final String url, final String key, Object initialValue, TitleSpec titleSpec, final IDetailsFactoryCallback callback) {
		super(parent, style);
		this.cardConfig = cardConfig;
		KeyValue keyValue = new KeyValue(key, initialValue);
		String name = Functions.call(cardConfig.nameFn, keyValue);
		titleLabel = new Title(this, cardConfig, titleSpec, name, url);
		setBackground(titleSpec.background);
		body = new Composite(this, SWT.NULL);
		body.setBackground(titleSpec.background);

		originalValue = Functions.call(cardConfig.valueFn, keyValue);
		editorControl = makeEditorControl(body, originalValue);
		okCancel = new OkCancel(body, cardConfig, new Runnable() {
			@Override
			public void run() {
				IMutableCardDataStore cardDataStore = (IMutableCardDataStore) cardConfig.cardDataStore;
				String value = getValue();
				if (!value.equals(originalValue))
					cardDataStore.put(url, Maps.<String, Object> makeMap(key, value), callback);
				ValueEditorComposite.this.dispose();

			}
		}, new Runnable() {
			@Override
			public void run() {
				ValueEditorComposite.this.dispose();
			}
		});
		updateEnabledStatusOfButtons();
		body.addPaintListener(new CardOutlinePaintListener(titleSpec, cardConfig, body));
		editorControl.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE)
					okCancel.cancel();
			}
		});

	}

	abstract protected T makeEditorControl(Composite parent, String originalValue);

	abstract protected String getValue();

	abstract protected void updateEnabledStatusOfButtons();

	abstract protected boolean useAllHeight();

	@Override
	public void setLayout(Layout layout) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Point computeSize(int wHint, int hHint) {
		Point textSize = editorControl.computeSize(wHint, hHint);
		Point okCancelSize = okCancel.getControl().computeSize(wHint, hHint);
		int height = hHint == SWT.DEFAULT ? cardConfig.titleHeight + textSize.y + okCancelSize.y : hHint;
		int width = getParent().getClientArea().width; // want full width if can have it
		return new Point(width, height);
	}

	@Override
	public void layout(boolean changed) {
		Rectangle clientArea = getClientArea();

		body.setBounds(clientArea.x + 1, //
				clientArea.y + 1 + cardConfig.titleHeight, //
				clientArea.width - 2,//
				clientArea.height - cardConfig.titleHeight - 2);// allows for line outside...

		Rectangle bodyClientArea = body.getClientArea();

		titleLabel.getControl().setSize(bodyClientArea.width, cardConfig.titleHeight);
		titleLabel.getControl().setLocation(bodyClientArea.x + 1, bodyClientArea.y + 1);

		Control okCancelControl = okCancel.getControl();
		okCancelControl.pack();
		int okCancelWidth = okCancelControl.getSize().x;
		int okCancelHeight = okCancelControl.getSize().y;
		okCancelControl.setBounds(//
				bodyClientArea.x + bodyClientArea.width - okCancelWidth - 2,//
				bodyClientArea.y + bodyClientArea.height - okCancelHeight - 2,//
				okCancelWidth, okCancelHeight);

		int editorHeight = useAllHeight() ? //
		clientArea.height - cardConfig.titleHeight - okCancelControl.getSize().y - cardConfig.topMargin - cardConfig.bottomMargin - 2
				: editorControl.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;

		editorControl.setBounds(bodyClientArea.x + 1 + cardConfig.leftMargin,//
				bodyClientArea.y + 1 + cardConfig.topMargin, //
				bodyClientArea.width - 2 - cardConfig.leftMargin - cardConfig.rightMargin, //
				editorHeight);
		Swts.layoutDump(getParent());

	}

}