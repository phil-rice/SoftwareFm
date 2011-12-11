package org.softwareFm.card.card.composites;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.collections.Lists;

public class StyledTextWithBold implements IHasControl {


	private final StyledText styledText;

	public StyledTextWithBold(Composite parent, int style) {
		styledText = new StyledText(parent, style);
	}

	public void setText(String text) {
		styledText.setText(text);
		List<StyleRange> ranges=Lists.newList();
		StringBuilder builder = new StringBuilder();
		int start = -1;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			switch (ch) {
			case '<':
				if (start != -1)
					throw new IllegalArgumentException(MessageFormat.format(CardConstants.cannotStyleText, text, i, ch));
				start = builder.length();;
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

}
