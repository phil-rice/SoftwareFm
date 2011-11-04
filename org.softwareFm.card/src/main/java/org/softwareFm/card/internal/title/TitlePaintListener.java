package org.softwareFm.card.internal.title;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.internal.details.TitleSpec;

public class TitlePaintListener implements PaintListener {
	private final CardConfig cardConfig;
	private TitleSpec titleSpec;
	private String title;

	public TitlePaintListener(CardConfig cardConfig, TitleSpec titleSpec, String title) {
		this.cardConfig = cardConfig;
		this.titleSpec = titleSpec;
		this.title = title;
	}

	@Override
	public void paintControl(PaintEvent e) {
//		e.gc.fillRectangle(e.x, e.y, e.width, e.height);
		e.gc.setBackground(titleSpec.background);
		e.gc.fillRoundRectangle(e.x, e.y, e.width - titleSpec.rightIndent, e.height + cardConfig.cornerRadius, cardConfig.cornerRadius, cardConfig.cornerRadius);

		if (titleSpec.icon != null)
			e.gc.drawImage(titleSpec.icon, e.x + cardConfig.titleSpacer, e.y + 1);
		int leftX = titleSpec.icon == null ? e.x + cardConfig.titleSpacer : e.x + 2 * cardConfig.titleSpacer + titleSpec.icon.getImageData().width;
		e.gc.setClipping(e.x, e.y, e.width - titleSpec.rightIndent - cardConfig.cornerRadius, e.height);
		e.gc.drawText(title, leftX, e.y);
		e.gc.setClipping((Rectangle) null);
		e.gc.drawRoundRectangle(e.x, e.y, e.width - titleSpec.rightIndent, e.height + cardConfig.cornerRadius, cardConfig.cornerRadius, cardConfig.cornerRadius);
	}

	public void setTitleAndTitleSpec(String title, TitleSpec titleSpec) {
		this.title = title;
		this.titleSpec = titleSpec;
	}

	public TitleSpec getTitleSpec() {
		return titleSpec;
	}

	public String getTitle() {
		return title;
	}
}