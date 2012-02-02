/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.images;

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
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.functions.IFunction1;
import org.springframework.core.io.ClassPathResource;

public class Images {

	private static List<Image> images = Lists.newList();

	public static Iterable<String> getNamesFor(Class<?> anchor, String anImageName) {
		try {
			File aFile = new ClassPathResource(anImageName, anchor).getFile();
			File directory = aFile.getParentFile();
			return Iterables.remove(//
					Iterables.map(Iterables.iterable(directory.list(Files.extensionFilter("png"))), Files.noExtension()), new IFunction1<String, Boolean>() {
						@Override
						public Boolean apply(String from) throws Exception {
							return from.endsWith("Inactive");
						}
					});
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void registerImagesInDirectory(Device device, ImageRegistry imageRegistry, Class<?> anchor, String prefix, String aName) {
		Iterable<String> names = getNamesFor(anchor, aName + ".png");
		for (String name : names)
			imageRegistry.put(prefix + "." + name, Images.makeImage(device, anchor, name + ".png"));
	}

	public static void registerImage(Device device, ImageRegistry imageRegistry, Class<?> clazz, String key) {
		Image mainImage = makeImage(device, clazz, key + ".png");
		imageRegistry.put(key, mainImage);
		System.out.println("Putting image: " + key);
	}

	public static Image getImage(ImageRegistry imageRegistry, String imageName) {
		Image image = imageRegistry.get(imageName);
		if (image == null)
			throw new IllegalArgumentException(MessageFormat.format(SoftwareFmImageConstants.cannotFindImage, imageName));
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

	public static Image makeImageOrNull(Device device, Class<?> clazz, String classPath) {
		try {
			ClassPathResource resource = new ClassPathResource(classPath, clazz);
			if (!resource.exists())
				return null;
			InputStream inputStream = resource.getInputStream();
			Image image = new Image(device, inputStream);
			images.add(image);
			return image;
		} catch (Exception e) {
			throw new RuntimeException(MessageFormat.format(SoftwareFmImageConstants.errorMakingImage, classPath, clazz.getName()), e);
		}
	}

	public static Image makeImage(Device device, Class<?> clazz, String classPath) {
		try {
			InputStream inputStream = new ClassPathResource(classPath, clazz).getInputStream();
			Image image = new Image(device, inputStream);
			images.add(image);
			return image;
		} catch (Exception e) {
			throw new RuntimeException(MessageFormat.format(SoftwareFmImageConstants.errorMakingImage, classPath, clazz.getName()), e);
		}
	}

	public static void registerImages(Device device, ImageRegistry imageRegistry, Class<?> anchor, String prefix, String... names) {
		for (String name : names) {
			String fullName = prefix + "." + name;
			Image image = makeImage(device, anchor, name + ".png");
			imageRegistry.put(fullName, image);
			Image imageInactive = makeImageOrNull(device, anchor, name + "Inactive.png");
			if (imageInactive != null)
				imageRegistry.put(fullName + ".inactive", imageInactive);
		}
	}

}