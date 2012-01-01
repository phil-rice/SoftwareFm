/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal;

import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CardToTitleSpecFn implements IFunction1<ICardData, TitleSpec> {
	private final IFunction1<String, Image> imageFn;
	private final Display display;
	private final Map<String, Color> colorCache = Maps.newMap();
	private final Map<String, Color> titleColorCache = Maps.newMap();

	public CardToTitleSpecFn(Display display, IFunction1<String, Image> imageFn) {
		this.imageFn = imageFn;
		this.display = display;
	}

	@Override
	public TitleSpec apply(final ICardData from) throws Exception {
		Color color = Maps.findOrCreate(colorCache, from.cardType(), new Callable<Color>() {
			@Override
			public Color call() throws Exception {
				return Swts.makeColor(display, Functions.call(from.getCardConfig().resourceGetterFn, from.cardType()), CardConstants.cardColorKey);
			}
		});
		Color titleColor = Maps.findOrCreate(titleColorCache, from.cardType(), new Callable<Color>() {
			@Override
			public Color call() throws Exception {
				return Swts.makeColor(display, Functions.call(from.getCardConfig().resourceGetterFn, from.cardType()), CardConstants.cardTitleColorKey);
			}
		});
		int indent = IResourceGetter.Utils.getIntegerOrException(from.getCardConfig().resourceGetterFn, from.cardType(), CardConstants.indentTitleKey);
		String imageName = IResourceGetter.Utils.getOrException(from.getCardConfig().resourceGetterFn, from.cardType(), CardConstants.cardTitleIcon);
		Image icon = Functions.call(imageFn, imageName);
		return new TitleSpec(icon, titleColor, color, indent);
	}

}