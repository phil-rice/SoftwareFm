package org.softwareFm.card.title;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.internal.CardToTitleSpecFn;
import org.softwareFm.softwareFmImages.Images;
import org.softwareFm.utilities.functions.IFunction1;

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

	public TitleSpec withoutImage() {
		return new TitleSpec(null, background, rightIndent);
	}

	public static IFunction1<ICard, TitleSpec> cardToTitleSpecFn(Display display, IFunction1<String, Image> imageFn) {
		return new CardToTitleSpecFn(display, imageFn);
	}
}
