/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.swt.card.ICardSelectedListener;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;

public class OneCardHolder implements IHasComposite {

	private final HoldsCardHolder content;

	public OneCardHolder(Composite parent, IContainer container, CardConfig cardConfig, String url, String title, ICardSelectedListener listener) {
		content = new HoldsCardHolder(parent, SWT.NULL, cardConfig, container);
		content.makeCardHolder(url, title);
		content.addCardSelectedListener(listener);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public Object getCardConfig() {
		return content.cardConfig;
	}

}