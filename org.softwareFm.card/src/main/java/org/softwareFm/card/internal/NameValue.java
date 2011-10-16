package org.softwareFm.card.internal;

import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ILine;

public class NameValue implements ILine {

	 final NameValueComposite content;

	public NameValue(Composite parent, CardConfig cardConfig, String name, String value) {
		content = new NameValueComposite(parent, SWT.NULL, cardConfig, name, value);
	}

	class NameValueComposite extends Composite {

		final CardConfig cardConfig;
		final String name;
		final String value;
		final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<Listener>();

		public NameValueComposite(Composite parent, int style, CardConfig cardConfig, String name, String value) {
			super(parent, style);
			this.cardConfig = cardConfig;
			this.name = name;
			this.value = value;
			Rectangle clientArea = getClientArea();

			Label lblName = new Label(this, SWT.NULL);
			lblName.setText(name);
			lblName.setLocation(clientArea.x + cardConfig.nameOffsetX, clientArea.y + cardConfig.nameOffsetY);
			lblName.setSize(cardConfig.nameWidth, cardConfig.textHeight);

			Text lblValue = new Text(this, SWT.NULL);
			lblValue.setText(value);
			lblValue.setLocation(clientArea.x + cardConfig.valueOffsetX, clientArea.y + cardConfig.valueOffsetY);
			lblValue.setSize(cardConfig.valueWidth, cardConfig.textHeight);
			addListeners(lblName, lblValue);
		}

		private Rectangle getChildAreaAllowingForTests() {
			return content.getClientArea();
		}

		private void addListeners(Control... controls) {
			for (Control control : controls)
				control.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseDown(MouseEvent e) {
						for (Listener listener : listeners) {
							Event event = new Event();
							listener.handleEvent(event);
						}
					}
				});
		}

	}

	@Override
	public void addSelectedListener(Listener listener) {
		content.listeners.add(listener);
	}

	@Override
	public Control getControl() {
		return content;
	}

}
