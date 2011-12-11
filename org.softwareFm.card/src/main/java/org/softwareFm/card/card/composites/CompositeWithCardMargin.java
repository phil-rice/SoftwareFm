package org.softwareFm.card.card.composites;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.IHasCardConfig;
import org.softwareFm.card.configuration.CardConfig;

public class CompositeWithCardMargin extends Composite implements IHasCardConfig{

	private final CardConfig cc;

	public CompositeWithCardMargin(Composite parent, int style, CardConfig cardConfig) {
		super(parent, style);
		this.cc = cardConfig;
	}

	@Override
	public Rectangle getClientArea() {
		Rectangle ca = super.getClientArea();
		return new Rectangle(ca.x + cc.leftMargin, ca.y + cc.topMargin, ca.width - cc.leftMargin - cc.rightMargin, ca.height - cc.topMargin - cc.bottomMargin);
	}

	@Override
	public CardConfig getCardConfig() {
		return cc;
	}
}
