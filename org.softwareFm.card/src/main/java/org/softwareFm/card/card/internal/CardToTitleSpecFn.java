package org.softwareFm.card.card.internal;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CardToTitleSpecFn implements IFunction1<ICard, TitleSpec> {
	private final IFunction1<String, Image> imageFn;
	private final Display display;

	public CardToTitleSpecFn(Display display, IFunction1<String, Image> imageFn) {
		this.imageFn = imageFn;
		this.display = display;
	}

	@Override
	public TitleSpec apply(ICard from) throws Exception {
		Color color = Swts.makeColor(display, Functions.call(from.cardConfig().resourceGetterFn, from.cardType()), CardConstants.cardColorKey);
		int indent = IResourceGetter.Utils.getIntegerOrException(from.cardConfig().resourceGetterFn, from.cardType(), CardConstants.indentTitleKey);
		String imageName = IResourceGetter.Utils.getOrException(from.cardConfig().resourceGetterFn, from.cardType(), CardConstants.cardTitleIcon);
		Image icon = Functions.call(imageFn, imageName);
		return new TitleSpec(icon, color, indent);
	}

}