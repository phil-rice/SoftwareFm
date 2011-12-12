package org.softwareFm.card.card.composites;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.IHasCardConfig;
import org.softwareFm.card.configuration.CardConfig;

public class CompositeWithEditorIndent extends Composite implements IHasCardConfig{

	private final CardConfig cc;

	public CompositeWithEditorIndent(Composite parent, int style, CardConfig cardConfig) {
		super(parent, style);
		this.cc = cardConfig;
	}

	@Override
	public Rectangle getClientArea() {
		Rectangle ca = super.getClientArea();
		return new Rectangle(ca.x + cc.editorIndentX, ca.y + cc.editorIndentY, ca.width - cc.editorIndentX*2, ca.height - cc.editorIndentY*2);
	}

	@Override
	public CardConfig getCardConfig() {
		return cc;
	}
}
