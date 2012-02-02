package org.softwareFm.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.ILineSelectedListener;
import org.softwareFm.swt.card.dataStore.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.internal.TextViewer;
import org.softwareFm.swt.editors.internal.TextViewer.TextViewLayout;
import org.softwareFm.swt.swt.Swts;

public class TextViewerMain {
	public static void main(String[] args) {
			Swts.Show.display(TextViewer.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					final SashForm sashForm = new SashForm(from, SWT.HORIZONTAL);
					CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
					ICard card = ICardFactory.Utils.createCardWithLayout(sashForm, cardConfig, CardDataStoreFixture.url1a, CardDataStoreFixture.data1a);
					final Composite right = new Composite(sashForm, SWT.NULL);
					right.setLayout(new FillLayout());
					sashForm.setWeights(new int[] { 1, 2 });
					card.addLineSelectedListener(new ILineSelectedListener() {
						@Override
						public void selected(ICard card, String key, Object value) {
							Swts.removeAllChildren(right);
							TextViewer textViewer = new TextViewer(right, card, key);
							textViewer.getComposite().setLayout(new TextViewLayout());
							right.layout();
							sashForm.layout();
						}
					});
					TextViewer textViewer = new TextViewer(right, card, "1a");
					textViewer.getComposite().setLayout(new TextViewLayout());
					return sashForm;
				}
			});
		}
}

