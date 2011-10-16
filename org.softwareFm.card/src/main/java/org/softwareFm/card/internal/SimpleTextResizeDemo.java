package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;

public class SimpleTextResizeDemo {

	public static void main(String[] args) {
		Swts.displayNoLayout("title", new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(Composite from) throws Exception {
				final Composite content = new Composite(from, SWT.BORDER);
				final Text text = new Text(content, SWT.NULL);
				text.setText("some text");
				content.setSize(200, 100);
				text.setSize(100, 25);
				Button go = Swts.makePushButton(content, new ResourceGetterMock("title", "Press me"),"title", new Runnable() {
					@Override
					public void run() {
						content.getParent().addListener(SWT.Resize, new Listener() {
							@Override
							public void handleEvent(Event event) {
								Rectangle clientArea = content.getParent().getClientArea();
								content.setSize(clientArea.width, clientArea.height);
								text.setSize(clientArea.width*2/3, 30);
							}
						});
					}
				});
				go.setSize(60, 20);
				go.setLocation(10, 50);
				return content;
			}
		});
	}
}
