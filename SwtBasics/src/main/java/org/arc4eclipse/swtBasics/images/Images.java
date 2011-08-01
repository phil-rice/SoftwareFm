package org.arc4eclipse.swtBasics.images;

import java.io.IOException;
import java.io.InputStream;

import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.springframework.core.io.ClassPathResource;

public class Images {

	private final Display display;
	private Image editImage;
	private Image linkImage;
	private Image browseImage;
	private Image clearImage;

	public Images(Display display) {
		this.display = display;
	}

	public void dispose() {
		dispose(editImage);
		dispose(linkImage);
		dispose(browseImage);
	}

	private void dispose(Image image) {
		if (image != null)
			image.dispose();
	}

	public Image getBrowseImage() {
		if (browseImage == null)
			browseImage = makeImage("Browse.png");
		return browseImage;
	}

	public Image getEditImage() {
		if (editImage == null)
			editImage = makeImage("Edit.png");
		return editImage;
	}

	public Image getLinkImage() {
		if (linkImage == null)
			linkImage = makeImage("Link.png");
		return linkImage;
	}
	public Image getClearImage() {
		if (clearImage == null)
			clearImage = makeImage("Clear.png");
		return clearImage;
	}

	private Image makeImage(String string) {
		try {
			InputStream inputStream = new ClassPathResource(string, getClass()).getInputStream();
			return new Image(display, inputStream);
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}


}
