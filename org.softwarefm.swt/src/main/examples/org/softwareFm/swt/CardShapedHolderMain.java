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

