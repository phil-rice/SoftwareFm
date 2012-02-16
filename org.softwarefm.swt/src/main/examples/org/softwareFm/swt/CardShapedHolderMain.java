/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.composites.CardShapedHolder;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.title.TitleSpec;

public class CardShapedHolderMain {
	public static void main(String[] args) {
	
		Swts.Show.display(CardShapedHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				CardShapedHolder<IHasControl> cardShapedHolder = new CardShapedHolder<IHasControl>(from, cardConfig, Swts.labelFn("Title"), Swts.styledTextFn("body", SWT.NULL));
				cardShapedHolder.setTitleSpec(TitleSpec.noTitleSpec(getColor(from, SWT.COLOR_CYAN), getColor(from, SWT.COLOR_GREEN)));
				return cardShapedHolder.getComposite();
			}
	
			private Color getColor(final Composite from, int color) {
				return from.getDisplay().getSystemColor(color);
			}
		});
	}
	
}