package org.softwareFm.softwareFmImages;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Device;

public interface IImageRegisterConfigurator {

	void registerWith(Device device, ImageRegistry imageRegistry);

	public static class Utils {
		public static ImageRegistry withBasics(Device device) {
			ImageRegistry result = new ImageRegistry();
			new BasicImageRegisterConfigurator().registerWith(device, result);
			return result;
		}
	}
}
