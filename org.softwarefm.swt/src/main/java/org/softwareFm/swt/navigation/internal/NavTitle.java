/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.navigation.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.navigation.ITitleBarForCard;
import org.softwareFm.swt.title.TitleSpec;
import org.softwareFm.swt.title.TitleWithTitlePaintListener;

public class NavTitle extends TitleWithTitlePaintListener implements ITitleBarForCard {

	public NavTitle(Composite parent, final CardConfig cardConfig, TitleSpec titleSpec, String initialLabel, String tooltip) {
		super(parent, cardConfig, titleSpec, initialLabel, tooltip);
	}

	@Override
	public ITransaction<?> setUrl(ICard card) {
		String title = (Functions.call(card.getCardConfig().cardTitleFn, card));
		TitleSpec titleSpec = Functions.call(card.getCardConfig().titleSpecFn, card);
		setTitleAndImage(title, card.url(), titleSpec);
		return ITransaction.Utils.doneTransaction(null);
	}

}