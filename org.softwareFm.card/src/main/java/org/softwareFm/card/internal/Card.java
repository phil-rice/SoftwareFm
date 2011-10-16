package org.softwareFm.card.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ILineFactory;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.swt.Swts;

public class Card implements ICard {

	final Group content;
	private final List<ILineSelectedListener> listeners = new CopyOnWriteArrayList<ILineSelectedListener>();
	Future<?> future;
	final CardConfig cardConfig;
	 final String url;

	public Card(Composite parent, CardConfig cardConfig, String url, String title) { // order matters in the map, so it's likely to be a linked map
		this.cardConfig = cardConfig;
		this.url = url;
		content = Swts.newGroup(parent, SWT.NULL, getClass().getSimpleName() + "/" + url);
		content.setSize(cardConfig.cardWidth, cardConfig.cardHeight);
		content.setText(title);
		new Loading(content);
	}

	@Override
	public Control getControl() {
		return content;
	}

	public void addKeyValueSelectedListener(ILineSelectedListener listener) {
		listeners.add(listener);
	}

	public void setFuture(Future<?> future) {
		this.future = future;

	}

	@Override
	public void addLineSelectedListener(ILineSelectedListener listener) {
		// TODO Auto-generated method stub

	}

	public void populate(ILineFactory lineFactory, List<KeyValue> list) {
		Swts.removeAllChildren(content);
		for (KeyValue keyValue : list)
			lineFactory.make(content, keyValue);
	}
}
