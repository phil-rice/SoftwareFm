package org.softwareFm.swtBasics.images;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.functions.IFunction1;

public class ImageButtonDemo {

	public static void main(String[] args) {
		final String mainImage = "test.mainImage";
		final String overlayImage = "test.overlayImage";
		final String smallIcon = "test.smallIcon";
		Swts.display("ImageButtonDemo", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite content = new Composite(from, SWT.NULL);
				content.setLayout(new GridLayout());
				ImageRegistry imageRegistry = Images.withBasics(from.getDisplay());
				imageRegistry.put(mainImage, Images.makeImage(from.getDisplay(), Images.class, mainImage + ".png"));
				imageRegistry.put(overlayImage, Images.makeImage(from.getDisplay(), Images.class, "test.overlayImage.png"));
				imageRegistry.put(smallIcon, Images.makeImage(from.getDisplay(), Images.class, "test.smallIcon.png"));

				final ImageButton imageButton = new ImageButton(content, imageRegistry, mainImage, false);
				final ImageButton overlayImageButton = new ImageButton(content, imageRegistry, mainImage, overlayImage, false);
				for (SmallIconPosition pos : SmallIconPosition.values())
					makeButton(content, imageButton, overlayImageButton, smallIcon, pos);
				final Button button = new Button(content, SWT.TOGGLE);
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
				return content;
			}

			private void makeButton(Composite content, final ImageButton imageButton, final ImageButton overlayImageButton, final String smallIconKey, final SmallIconPosition pos) {
				final Button button = new Button(content, SWT.TOGGLE);
				button.setText(pos.name());
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						boolean state = button.getSelection();
						String key = state ? smallIconKey : null;
						imageButton.setSmallIcon(pos, key);
						imageButton.setSmallIcon(pos, key);
						overlayImageButton.setSmallIcon(pos, key);
					}
				});
			}
		});
	}
}
