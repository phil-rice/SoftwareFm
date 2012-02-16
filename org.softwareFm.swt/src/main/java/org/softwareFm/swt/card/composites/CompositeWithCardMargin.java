/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.composites;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swt.card.IHasCardConfig;
import org.softwareFm.swt.configuration.CardConfig;

public class CompositeWithCardMargin extends Composite implements IHasCardConfig {

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