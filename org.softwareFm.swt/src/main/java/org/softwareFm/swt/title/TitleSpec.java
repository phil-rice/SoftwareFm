/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.title;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.internal.CardToTitleSpecFn;

public class TitleSpec {
	public final Image icon;
	public final Color titleColor;
	public final Color background;
	public final int rightIndent;

	public TitleSpec(Image icon, Color titleColor, Color background, int rightIndent) {
		this.icon = icon;
		this.titleColor = titleColor;
		this.background = background;
		this.rightIndent = rightIndent;
	}

	public static TitleSpec noTitleSpec(Color titleColor, Color background) {
		return new TitleSpec(null, titleColor, background, 40);
	}

	public static TitleSpec noTitleSpec(Color color) {
		return new TitleSpec(null, color, color, 40);
	}

	public TitleSpec withoutImage() {
		return new TitleSpec(null, titleColor, background, rightIndent);
	}

	public static IFunction1<ICardData, TitleSpec> cardToTitleSpecFn(Display display, IFunction1<String, Image> imageFn) {
		return new CardToTitleSpecFn(display, imageFn);
	}
}