package org.softwarefm.core.constants;

import java.io.InputStream;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwarefm.core.image.ArtifactsAnchor;

public class ImageConstants {
	public static final String exclamationAction = "exclamationActive";
	public static final String exclamationInaction = "exclamationInactive";

	public static final String backButton = "backEclipse";
	public static final String stopButton = "stopEclipse";
	public static final String forwardButton = "forwardEclipse";
	public static final String refreshButton = "refreshEclipse";
	public static final String goButton = "goEclipse";

	public static void initializeImageRegistry(Display display, ImageRegistry reg) {
		addImage(display, reg, ImageConstants.exclamationAction);
		addImage(display, reg, ImageConstants.exclamationInaction);
		addImage(display, reg, ImageConstants.backButton);
		addImage(display, reg, ImageConstants.stopButton);
		addImage(display, reg, ImageConstants.forwardButton);
		addImage(display, reg, ImageConstants.refreshButton);
		addImage(display, reg, ImageConstants.goButton);
	}

	private static void addImage(Display display, ImageRegistry reg, String key) {
		try {
			InputStream stream = ArtifactsAnchor.class.getResourceAsStream(key + ".png");
			try {
				reg.put(key, new Image(display, stream));
			} finally {
				stream.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("addImage(" + key + ")", e);
		}
	}

}
