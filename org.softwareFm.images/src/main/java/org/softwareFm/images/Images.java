/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.images;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.softwareFm.common.collections.Lists;
import org.springframework.core.io.ClassPathResource;

public class Images {

	private static List<Image> images = Lists.newList();


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