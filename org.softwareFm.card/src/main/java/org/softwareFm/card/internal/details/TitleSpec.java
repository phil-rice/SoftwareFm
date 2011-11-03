package org.softwareFm.card.internal.details;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.softwareFm.softwareFmImages.Images;

public class TitleSpec {
	public final Image icon;
	public final Color background;
	public final int rightIndent;

	public TitleSpec(Image icon, Color background, int rightIndent) {
		this.icon = icon;
		this.background = background;
		this.rightIndent = rightIndent;
	}

	public TitleSpec(ImageRegistry imageRegistry, String icon, Color background, int rightIndent) {
		this.background = background;
		this.rightIndent = rightIndent;
		this.icon = Images.getImage(imageRegistry, icon);
	}

	public static TitleSpec noTitleSpec(Color background) {
		return new TitleSpec(null, background, 40);
	}
}
