package org.softwareFm.card.internal.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.display.composites.IHasControl;

public class Title implements IHasControl {

	private final Canvas canvas;
	private String title;
	private Image image;

	public Title(Composite parent, final CardConfig cardConfig, String initialLabel, String tooltip) {
		this.title = initialLabel;
		canvas = new Canvas(parent, SWT.NULL);
		canvas.setToolTipText(tooltip);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (image == null)
					e.gc.drawText(title, 0, 0);
				else {
					e.gc.drawImage(image, 0, 0);
					e.gc.drawText(title, image.getImageData().width + cardConfig.iconToTextSpacer, 0);
				}
			}
		});
	}

	public void setTitleAndImage(String title, Image image) {
		this.image = image;
		this.title = title;
		canvas.redraw();
	}

	public void setTitle(String title) {
		this.title = title;
		canvas.redraw();
	}

	public void setIcon(Image image) {
		this.image = image;
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
