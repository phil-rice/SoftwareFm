package org.softwareFm.card.internal;

import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
		 final Label lblName;
		 final Text txtValue;

		public NameValueComposite(Composite parent, int style, CardConfig cardConfig, String name, String value) {
			super(parent, style);
			if (cardConfig.debugLayout)
				setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GREEN));
			this.cardConfig = cardConfig;
			this.name = name;
			this.value = value;

			lblName = new Label(this, SWT.NULL);
			lblName.setText(name);

			txtValue = new Text(this, SWT.NULL);
			txtValue.setText(value);

			addListeners(this, lblName, txtValue);
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

	@Override
	public void setWidth(int width, int titleWidth) {
		content.setSize(width, content.cardConfig.lineHeight);
		int valueWidth = width - titleWidth - content.cardConfig.nameValueGap - 2 * content.cardConfig.lineMarginX;

		content.lblName.setSize(titleWidth, content.cardConfig.textHeight);
		content.lblName.setLocation(content.cardConfig.lineMarginX, content.cardConfig.lineMarginY);
		content.txtValue.setSize(valueWidth, content.cardConfig.textHeight);
		content.txtValue.setLocation(titleWidth + content.cardConfig.nameValueGap, content.cardConfig.lineMarginY);

	}

	@Override
	public int preferredTitleWidth() {
		return content.lblName.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
	}

}
