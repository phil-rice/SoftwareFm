package org.softwareFm.swtBasics.images;

import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.swtBasics.artifacts.ArtifactsAnchor;
import org.softwareFm.swtBasics.backdrop.BackdropAnchor;
import org.softwareFm.swtBasics.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.smallIcons.SmallIconsAnchor;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.springframework.core.io.ClassPathResource;

public class Images {

	private static List<Image> images = Lists.newList();

	public static ImageRegistry withBasics(Device device) {
		ImageRegistry imageRegistry = new ImageRegistry();
		registerImages(device, imageRegistry, Images.class, SwtBasicConstants.key);
		registerImages(device, imageRegistry, Images.class, SwtBasicConstants.helpKey);
		registerImages(device, imageRegistry, Images.class, SwtBasicConstants.addKey);
		registerImages(device, imageRegistry, Images.class, SwtBasicConstants.deleteKey);
		registerImages(device, imageRegistry, Images.class, SwtBasicConstants.browseKey);
		registerImages(device, imageRegistry, Images.class, SwtBasicConstants.clearKey);

		registerImagesInDirectory(device, imageRegistry, ArtifactsAnchor.class, "artifact", "jar");
		registerImagesInDirectory(device, imageRegistry, BackdropAnchor.class, "backdrop", "main");
		registerImagesInDirectory(device, imageRegistry, OverlaysAnchor.class, "overlay", "add");
		registerImagesInDirectory(device, imageRegistry, SmallIconsAnchor.class, "smallIcon", "javadoc");
		return imageRegistry;
	}

	public static Iterable<String> getNamesFor(Class<?> anchor, String anImageName) {
		try {
			File aFile = new ClassPathResource(anImageName, anchor).getFile();
			File directory = aFile.getParentFile();
			return Iterables.map(Iterables.iterable(directory.list(Files.extensionFilter("png"))), Files.noExtension());
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void registerImagesInDirectory(Device device, ImageRegistry imageRegistry, Class<?> anchor, String prefix, String aName) {
		for (String name : getNamesFor(anchor, aName + ".png"))
			imageRegistry.put(prefix + "." + name, Images.makeImage(device, anchor, name + ".png"));
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

	public static Image makeTransparentImage(Device device, Class<?> clazz, String classPath) {
		ImageData imageData = new ImageData(clazz.getResourceAsStream(classPath));
		int whitePixel = imageData.palette.getPixel(new RGB(255, 255, 255));
		imageData.transparentPixel = whitePixel;
		Image transparentImage = new Image(device, imageData);
		images.add(transparentImage);
		return transparentImage;
	}

	public static void drawTransparentImageOver(Label label, final Image transparentImage) {
		label.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(transparentImage, 0, 0);
			}
		});
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
