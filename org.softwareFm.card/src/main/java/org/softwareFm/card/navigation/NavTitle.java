package org.softwareFm.card.navigation;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;

public class NavTitle implements ITitleBarForCard {

	private final Canvas canvas;
	private String title;
	private Image image;

	public NavTitle(Composite parent, final CardConfig cardConfig,String initialLabel) {
		this.title = initialLabel;
		canvas = new Canvas(parent, SWT.NULL);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (image == null)
					e.gc.drawText(title, 0, 0);
				else {
					e.gc.drawImage(image, 0, 0);
					e.gc.drawText(title, image.getImageData().width+cardConfig.iconToTextSpacer, 0);
				}
			}
		});
	}

	@Override
	public void setUrl(ICard card) {
		IFunction1<Map<String, Object>, Image> cardIconFn = card.cardConfig().cardIconFn;
		image = Functions.call(cardIconFn, card.rawData());
		title = (Functions.call(card.cardConfig().cardTitleFn, card.url()));
		canvas.redraw();
	}

	@Override
	public Control getControl() {
		return canvas;
	}

	public String getText() {
		return title;
	}

}
