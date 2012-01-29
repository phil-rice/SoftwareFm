/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.composites;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.collections.Lists;

public class StyledTextWithBold implements IHasControl {

	public static class StyledTextWithBoldComposite extends Composite {

		private final CardConfig cc;

		public StyledTextWithBoldComposite(Composite parent, int style, CardConfig cardConfig) {
			super(parent, style);
			this.cc = cardConfig;
		}

		@Override
		public Rectangle getClientArea() {
			Rectangle ca = super.getClientArea();
			return new Rectangle(ca.x + cc.editorIndentX, ca.y + cc.editorIndentY, ca.width - 2 * cc.editorIndentX, ca.height - 2 * cc.editorIndentY);
		}

	}

	private final Composite content;
	private final StyledText styledText;

	public StyledTextWithBold(Composite parent, int textStyle, CardConfig cardConfig) {
		content = new StyledTextWithBoldComposite(parent, SWT.NULL, cardConfig);
		styledText = new StyledText(content, textStyle);
		content.setLayout(new FillLayout());
	}

	public void setText(String text) {
		styledText.setText(text);
		List<StyleRange> ranges = Lists.newList();
		StringBuilder builder = new StringBuilder();
		int start = -1;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			switch (ch) {
			case '<':
				if (start != -1)
					throw new IllegalArgumentException(MessageFormat.format(CardConstants.cannotStyleText, text, i, ch));
				start = builder.length();
				break;
			case '>':
				if (start == -1)
					throw new IllegalArgumentException(MessageFormat.format(CardConstants.cannotStyleText, text, i, ch));
				StyleRange range = new StyleRange();
				range.start = start;
				range.length = builder.length() - start;
				range.fontStyle = SWT.BOLD;
				start = -1;
				ranges.add(range);
				break;
			default:
				builder.append(ch);
				break;
			}
		}
		styledText.setText(builder.toString());
		styledText.setStyleRanges(ranges.toArray(new StyleRange[0]));
	}

	@Override
	public Control getControl() {
		return styledText;
	}

	public void setBackground(Color background) {
		content.setBackground(background);
		// styledText.setBackground(background);
	}

}