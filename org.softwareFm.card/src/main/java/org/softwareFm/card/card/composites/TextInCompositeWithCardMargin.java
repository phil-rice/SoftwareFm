/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.composites;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class TextInCompositeWithCardMargin implements IHasControl {

	public static IFunction1<Composite, IHasControl> makeText(final int textStyle, final CardConfig cardConfig, final String key, final Object... args) {
		return makeText(textStyle, cardConfig, null, key, args);
	}

	public static IFunction1<Composite, IHasControl> makeText(final int textStyle, final CardConfig cardConfig, final Runnable whenClicked, final String cardType, final String titleKey, final String bodyKey, final Object... args) {
		return new IFunction1<Composite, IHasControl>() {
			@Override
			public IHasControl apply(Composite from) throws Exception {
				TextInBorder result = new TextInBorder(from, textStyle, cardConfig);
				result.setTextFromResourceGetter(cardType, titleKey, bodyKey, args);
				if (whenClicked != null)
					result.addClickedListener(whenClicked);
				return result;
			}
		};
	}

	private final CardConfig cardConfig;
	private final Composite content;
	private final StyledTextWithBold textWithBold;

	public TextInCompositeWithCardMargin(Composite parent, int textStyle, final CardConfig cardConfig) {
		this.cardConfig = cardConfig;
		content = new CompositeWithCardMargin(parent, SWT.NULL, cardConfig);
		content.setLayout(new FillLayout());
		textWithBold = new StyledTextWithBold(content, SWT.WRAP | SWT.READ_ONLY, cardConfig);
		content.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				Rectangle ca = content.getClientArea();
				e.gc.drawRoundRectangle(ca.x - cardConfig.cornerRadiusComp, //
						ca.y - cardConfig.cornerRadiusComp, //
						ca.width + 2 * cardConfig.cornerRadiusComp, //
						ca.height + 2 * cardConfig.cornerRadiusComp,//
						cardConfig.cornerRadius, cardConfig.cornerRadius);
			}
		});
	}

	public void setTextFromResourceGetter(String cardType, String patternKey, Object... args) {
		String pattern = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, patternKey);
		String text = MessageFormat.format(pattern, args);
		textWithBold.setText(text);
	}

	public void addClickedListener(final Runnable whenClicked) {
		textWithBold.getControl().addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				whenClicked.run();
			}
		});
	}

	@Override
	public Control getControl() {
		return content;
	}

}