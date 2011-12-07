package org.softwareFm.card.editors.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.internal.CardOutlinePaintListener;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.title.TitleSpec;

public class ValueEditorBodyComposite extends Composite {

	 final CardConfig cardConfig;
	final Composite innerBody;
	 final TitleSpec titleSpec;

	public ValueEditorBodyComposite(Composite parent, CardConfig cardConfig, TitleSpec titleSpec) {
		super(parent, SWT.NULL);
		this.cardConfig = cardConfig;
		this.titleSpec = titleSpec;
		this.innerBody = new Composite(this, SWT.NULL);
		innerBody.setBackground(titleSpec.background);
		addPaintListener(new CardOutlinePaintListener(titleSpec, cardConfig));

	}

	@Override
	public Rectangle getClientArea() {
		// note that the topMargin doesn't reference this component: it affects the space between the top of somewhere and the title.
		// There is a two pixel gap between the top of the card and the title
		Rectangle clientArea = super.getClientArea();
		int cardWidth = clientArea.width - cardConfig.rightMargin - cardConfig.leftMargin;
		int cardHeight = clientArea.height - cardConfig.bottomMargin - 2;
		Rectangle result = new Rectangle(clientArea.x + cardConfig.leftMargin, clientArea.y + 2, cardWidth, cardHeight);
		return result;
	}

	public Composite getInnerBody() {
		return innerBody;
	}

}