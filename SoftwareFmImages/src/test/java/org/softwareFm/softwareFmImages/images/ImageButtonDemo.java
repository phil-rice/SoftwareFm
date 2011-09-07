package org.softwareFm.softwareFmImages.images;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwareFm.utilities.functions.IFunction1;

public class ImageButtonDemo {

	public static void main(String[] args) {
		final String mainImage = "test.mainImage";
		final String alterativeImage = "test.alternativeImage";
		final String overlayImage = "test.overlayImage";
		final String smallIcon = "test.smallIcon";
		Swts.display("ImageButtonDemo", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite content = new Composite(from, SWT.NULL);
				content.setLayout(new GridLayout());
				ImageRegistry imageRegistry = SoftwareFmImages.withBasics(from.getDisplay());
				imageRegistry.put(mainImage, Images.makeImage(from.getDisplay(), Images.class, mainImage + ".png"));
				imageRegistry.put(alterativeImage, Images.makeImage(from.getDisplay(), Images.class, alterativeImage + ".png"));
				imageRegistry.put(overlayImage, Images.makeImage(from.getDisplay(), Images.class, "test.overlayImage.png"));
				imageRegistry.put(smallIcon, Images.makeImage(from.getDisplay(), Images.class, "test.smallIcon.png"));

				final ImageButton imageButton = new ImageButton(content, imageRegistry, mainImage, true);
				final ImageButton overlayImageButton = new ImageButton(content, imageRegistry, mainImage, overlayImage, true);
				Button toggleDepressedButton = new Button(content, SWT.PUSH);
				toggleDepressedButton.setText("Toggle Depressed");
				toggleDepressedButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						imageButton.setState(!imageButton.getState());
						boolean newState = !overlayImageButton.getState();
						overlayImageButton.setState(newState);
					}
				});
				Button toggleMainImageButton = new Button(content, SWT.PUSH);
				toggleMainImageButton.setText("Toggle Main Image");
				toggleMainImageButton.addSelectionListener(new SelectionAdapter() {
					boolean state = false;

					@Override
					public void widgetSelected(SelectionEvent e) {
						state = !state;
						imageButton.setImage(state ? alterativeImage : mainImage);
						overlayImageButton.setImage(state ? alterativeImage : mainImage);
					}
				});
				for (SmallIconPosition pos : SmallIconPosition.values())
					makeButton(content, imageButton, overlayImageButton, smallIcon, pos);
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
