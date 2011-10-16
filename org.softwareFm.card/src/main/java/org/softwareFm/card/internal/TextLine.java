package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ILine;

public class TextLine implements ILine {

	static class LoadingComposite extends Composite {
		private final Label label;

		public LoadingComposite(Composite parent, CardConfig cardConfig, String text) {
			super(parent, SWT.NULL);
			label = new Label(this, SWT.NULL);
			label.setText(text);
			label.setLocation(cardConfig.lineMarginX, cardConfig.lineMarginY);
			Rectangle clientArea = label.getParent().getClientArea();
			label.getParent().addListener(SWT.Resize, new Listener() {
				@Override
				public void handleEvent(Event event) {
					Rectangle clientArea = label.getParent().getClientArea();
					label.setSize(clientArea.width, clientArea.height);
				}
			});
		}
	}

	private final LoadingComposite content;
	private final CardConfig cardConfig;

	public TextLine(Composite parent, CardConfig cardConfig, String text) {
		this.cardConfig = cardConfig;
		content = new LoadingComposite(parent, cardConfig, text);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public void addSelectedListener(Listener listener) {
	}

	@Override
	public void setWidth(int width, int titleWidth) {
		content.setSize(width, cardConfig.lineHeight);
		content.label.setSize(width-cardConfig.lineMarginX*2, cardConfig.textHeight);
	}

	@Override
	public int preferredTitleWidth() {
		return content.label.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
	}

}
