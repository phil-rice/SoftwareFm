package org.softwareFm.softwareFmImages.images;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.utilities.functions.IFunction1;

public class TransparentImageTest {

	public static void main(String[] args) {

		Swts.display("TransparentImageTest", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				composite.setLayout(new GridLayout());
				Label label = new Label(composite, SWT.NULL);
				final Image main = Images.makeImage(from.getDisplay(), Images.class, "test.mainImage.main.png");
				label.setImage(main);
				final Image raw = Images.makeTransparentImage(from.getDisplay(), Images.class, "test.overlayImage.png");
				label.addPaintListener(new PaintListener() {
					@Override
					public void paintControl(PaintEvent e) {
						e.gc.drawImage(raw, 0, 0);
					}
				});
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;

			}

		});
	}
}
