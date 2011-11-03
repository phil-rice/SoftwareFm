package org.softwareFm.card.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.IMutableCardDataStore;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.internal.details.IDetailsFactoryCallback;
import org.softwareFm.card.internal.details.TitleSpec;
import org.softwareFm.card.internal.title.Title;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.maps.Maps;

abstract class ValueEditorComposite<T extends Control> extends Composite {

	public Title titleLabel;
	final T editorControl;
	protected final OkCancel okCancel;
	protected final CardConfig cardConfig;
	protected final String originalValue;

	public ValueEditorComposite(Composite parent, int style, final CardConfig cardConfig, final String url, final String key, Object initialValue, TitleSpec titleSpec, final IDetailsFactoryCallback callback) {
		super(parent, style);
		this.cardConfig = cardConfig;
		KeyValue keyValue = new KeyValue(key, initialValue);
		String name = Functions.call(cardConfig.nameFn, keyValue);
		titleLabel = new Title(this, cardConfig, titleSpec, name, url);
		setBackground(titleSpec.background);

		originalValue = Functions.call(cardConfig.valueFn, keyValue);
		editorControl = makeEditorControl(originalValue);
		okCancel = new OkCancel(this, cardConfig, new Runnable() {
			@Override
			public void run() {
				IMutableCardDataStore cardDataStore = (IMutableCardDataStore) cardConfig.cardDataStore;
				cardDataStore.put(url, Maps.<String, Object> makeMap(key, getValue()), callback);
				ValueEditorComposite.this.dispose();

			}
		}, new Runnable() {
			@Override
			public void run() {
				ValueEditorComposite.this.dispose();
			}
		});

		updateEnabledStatusOfButtons();
	}

	abstract protected T makeEditorControl(String originalValue);

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
		int height = cardConfig.titleHeight + textSize.y + okCancelSize.y;
		int width = getParent().getClientArea().width;
		return new Point(width, height);
	}

	@Override
	public void layout(boolean changed) {
		Rectangle clientArea = getClientArea();
		int width = clientArea.width - cardConfig.leftMargin - cardConfig.rightMargin;
		titleLabel.getControl().setSize(width, cardConfig.titleHeight);
		int startY = clientArea.y + cardConfig.topMargin;
		int x = clientArea.x + cardConfig.leftMargin;

		titleLabel.getControl().setLocation(x, startY);

		Control okCancelControl = okCancel.getControl();
		okCancelControl.pack();
		Point okCancelControlSize = okCancelControl.getSize();

		int textY = startY + cardConfig.titleHeight;
		editorControl.setLocation(x, textY);
		int textHeight = useAllHeight() ? clientArea.height - cardConfig.titleHeight - okCancelControlSize.y -cardConfig.topMargin-cardConfig.bottomMargin: editorControl.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		editorControl.setSize(width, textHeight);

		int okCancelWidth = okCancelControlSize.x;
		int okCancelY = textY + textHeight;
		okCancelControl.setLocation(x + width - okCancelWidth, okCancelY);

	}

}