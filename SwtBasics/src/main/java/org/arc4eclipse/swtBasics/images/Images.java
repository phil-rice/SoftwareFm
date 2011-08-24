package org.arc4eclipse.swtBasics.images;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.utilities.collections.Lists;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.springframework.core.io.ClassPathResource;

abstract public class Images {

	private static List<Image> images = Lists.newList();

	public static ImageRegistry withBasics(Device device) {
		ImageRegistry imageRegistry = new ImageRegistry();
		registerImages(device, imageRegistry, Images.class, SwtBasicConstants.editKey);
		registerImages(device, imageRegistry, Images.class, SwtBasicConstants.helpKey);
		registerImages(device, imageRegistry, Images.class, SwtBasicConstants.addKey);
		registerImages(device, imageRegistry, Images.class, SwtBasicConstants.deleteKey);
		registerImages(device, imageRegistry, Images.class, SwtBasicConstants.browseKey);
		return imageRegistry;
	}

	public static void registerImages(Device device, ImageRegistry imageRegistry, Class<?> clazz, String key) {
		String mainName = Resources.getMainName(key);
		String depressedName = Resources.getDepressedName(key);
		Image mainImage = makeImage(device, clazz, mainName + ".png");
		Image depressedImage = makeImage(device, clazz, depressedName + ".png");
		imageRegistry.put(mainName, mainImage);
		imageRegistry.put(depressedName, depressedImage);
		System.out.println("Putting images: " + mainName + ", " + depressedName);
	}

	public static void removeImages(ImageRegistry imageRegistry, String key) {
		if (key == null)
			return;
		String mainName = Resources.getMainName(key);
		String depressedName = Resources.getDepressedName(key);
		imageRegistry.remove(mainName);
		imageRegistry.remove(depressedName);
		System.out.println("Removing images: " + mainName + ", " + depressedName);
	}

	public static Image getMainImage(ImageRegistry imageRegistry, String key) {
		return getImage(imageRegistry, Resources.getMainName(key));
	}

	public static Image getDepressedImage(ImageRegistry imageRegistry, String key) {
		return getImage(imageRegistry, Resources.getDepressedName(key));
	}

	public static Image getImage(ImageRegistry imageRegistry, String imageName) {
		Image image = imageRegistry.get(imageName);
		if (image == null)
			throw new IllegalArgumentException(MessageFormat.format(SwtBasicConstants.cannotFindImage, imageName));
		return image;
	}

	public static void disposeAllMadeImages() {
		for (Image image : images)
			image.dispose();
		images.clear();

	}

	public static Image makeImage(Device device, Class<?> clazz, String classPath) {
		try {
			InputStream inputStream = new ClassPathResource(classPath, clazz).getInputStream();
			Image image = new Image(device, inputStream);
			images.add(image);
			return image;
		} catch (Exception e) {
			throw new RuntimeException(MessageFormat.format(SwtBasicConstants.errorMakingImage, classPath, clazz.getName()), e);
		}
	}

}
