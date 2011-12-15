/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
		return new Rectangle(ca.x + cc.editorIndentX, ca.y , ca.width - cc.editorIndentX*2, ca.height - cc.editorIndentY);
	}

	@Override
	public CardConfig getCardConfig() {
		return cc;
	}
}