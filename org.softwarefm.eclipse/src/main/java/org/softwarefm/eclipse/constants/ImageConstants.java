package org.softwarefm.eclipse.constants;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwarefm.eclipse.image.ArtifactsAnchor;
import org.softwarefm.utilities.exceptions.WrappedException;


public class ImageConstants {
	public static final String exclamationAction = "exclamationActive";
	public static final String exclamationActionPng = exclamationAction + ".png";

	public static final String exclamationInaction = "exclamationInactive";
	public static final String exclamationInactionPng = exclamationInaction + ".png";

	public static void initializeImageRegistry(Display display, ImageRegistry reg) {
		addImage(display, reg, ImageConstants.exclamationAction, ImageConstants.exclamationActionPng);
		addImage(display, reg, ImageConstants.exclamationInaction, ImageConstants.exclamationInactionPng);
	}

	private static void addImage(Display display, ImageRegistry reg, String key, String png) {
		try {
			InputStream stream = ArtifactsAnchor.class.getResourceAsStream(png);
			try {
				reg.put(key, new Image(display, stream));
			} finally {
				stream.close();
			}
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

}
