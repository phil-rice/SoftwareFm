package org.arc4eclipse.swtBasics.images;

import org.eclipse.swt.graphics.Device;

public interface IImageFactory {

	Images makeImages(Device device);

	public static class Utils {
		public static IImageFactory imageFactory() {
			return new IImageFactory() {
				@Override
				public Images makeImages(Device device) {
					return new Images(device);
				}
			};
		}
	}

}
