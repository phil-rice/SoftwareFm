package org.softwareFm.card.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Size;

public class ScrollingCardCollectionHolder implements IHasControl {

	private final ScrolledComposite content;
	private final CardCollectionHolder holder;

	public ScrollingCardCollectionHolder(Composite parent, CardConfig cardConfig) {
		this.content = Swts.newScrolledComposite(parent, SWT.H_SCROLL, getClass().getSimpleName());
		holder = new CardCollectionHolder(content, cardConfig);
		holder.getComposite().setLayout(new CardConfigFillWithAspectRatioLayout());
		Size.setSizeFromClientArea(content);
		content.setContent(holder.getControl());
		final Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				setSize();
			}

		};
		content.getParent().addListener(SWT.Resize, listener);
		content.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				content.getParent().removeListener(SWT.Resize, listener);
			}
		});

	}

	private void setSize() {
		Point hs1 = content.getHorizontalBar().getSize();
		Rectangle clientArea = content.getParent().getClientArea();
		content.setSize(clientArea.width, clientArea.height );
		Rectangle parentClientArea = clientArea;
		int height = parentClientArea.height;
		Point size = holder.getComposite().computeSize(SWT.DEFAULT, height);
		holder.getControl().setSize(size.x, size.y-hs1.y);
	}

	@Override
	public Control getControl() {
		return content;
	}

	public void setKeyValue(String newUrl, String key, Object value, IDetailsFactoryCallback callback) {
		holder.setKeyValue(newUrl, key, value, callback);
		setSize();
	}

	public CardCollectionHolder getCardHolder() {
		return holder;
	}

}
