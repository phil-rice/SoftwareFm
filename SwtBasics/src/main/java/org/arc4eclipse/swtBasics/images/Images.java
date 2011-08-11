package org.arc4eclipse.swtBasics.images;

import java.io.IOException;
import java.io.InputStream;

import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.springframework.core.io.ClassPathResource;

public class Images {

	private final Device device;
	private Image editImage;
	private Image linkImage;
	private Image browseImage;
	private Image clearImage;
	private Image helpImage;
	private Image addImage;
	private Image nameImage;
	private Image projectImage;
	private Image projectDepressedImage;

	public Images(Device device) {
		this.device = device;
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
			editImage = makeImage("edit document.png");
		return editImage;
	}

	public Image getHelpImage() {
		if (helpImage == null)
			helpImage = makeImage("Help.png");
		return helpImage;
	}

	public Image getLinkImage() {
		if (linkImage == null)
			linkImage = makeImage("link.png");
		return linkImage;
	}

	public Image getClearImage() {
		if (clearImage == null)
			clearImage = makeImage("delete document.png");
		return clearImage;
	}

	public Image getAddImage() {
		if (addImage == null)
			addImage = makeImage("+document.png");
		return addImage;
	}

	public Image getNameImage() {
		if (nameImage == null)
			nameImage = makeImage("name.png");
		return nameImage;
	}

	public Image getDescriptionImage() {
		if (nameImage == null)
			nameImage = makeImage("Description.png");
		return nameImage;
	}

	public Image getProjectImage() {
		if (projectImage == null)
			projectImage = makeImage("Project.png");
		return projectImage;
	}

	public Image getProjectDepressedImage() {
		if (projectDepressedImage == null)
			projectDepressedImage = makeImage("Project depress.png");
		return projectDepressedImage;
	}

	protected Image makeImage(String string) {
		try {
			InputStream inputStream = new ClassPathResource(string, getClass()).getInputStream();
			return new Image(device, inputStream);
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

}
