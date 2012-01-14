/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.internal.BasicCardConfigurator;
import org.softwareFm.card.dataStore.IMutableCardDataStore;
import org.softwareFm.card.dataStore.internal.CardDataStoreForRepository;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class CardUnit implements IHasComposite {
	public final static String rootUrl = "/softwareFm/data";
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final static Map<String, Object> urls = (Map) Maps.makeLinkedMapFromArray(Functions.addToStart(rootUrl),//
			"/org/apache/org.apache", "/", "/org/antlr/org.antlr", "/org/apache/ant/org.apache.ant", "/org/apache/ant/org.apache.ant/artifact/ant");
	private final SashForm sashForm;
	private final ICardHolder cardHolder;
	private final Text text;
	private final CardConfig cardConfig;

	public CardUnit(Composite parent, CardConfig cardConfig, List<String> rootUrls) {
		this.cardConfig = cardConfig;
		sashForm = new SashForm(parent, SWT.VERTICAL);
		cardHolder = ICardHolder.Utils.cardHolderWithLayout(sashForm, cardConfig, rootUrls, null);
		text = new Text(sashForm, SWT.WRAP);
	}

	public static void main(String[] args) {
		final IRepositoryFacard facard = null;// IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		try {

			Show.xUnit(CardUnit.class.getSimpleName(), new ISituationListAndBuilder<CardUnit>() {
				@Override
				public void selected(final CardUnit cardunit, String context, Object value) throws Exception {
					ICardFactory.Utils.makeCard(cardunit.cardHolder, cardunit.cardConfig, Strings.nullSafeToString(value), new ICallback<ICard>() {
						@Override
						public void process(ICard t) throws Exception {
							cardunit.text.setText(t.data().toString());
						}
					});
				}

				@Override
				public CardUnit makeChild(Composite parent) throws Exception {
					final IMutableCardDataStore cardDataStore = new CardDataStoreForRepository(parent, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = new BasicCardConfigurator().configure(parent.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					CardUnit cardUnit = new CardUnit(parent, cardConfig, Arrays.asList(rootUrl));
					return cardUnit;
				}
			}, urls);
		} finally {
			if (facard != null)
				facard.shutdown();
		}
	}

	@Override
	public Control getControl() {
		return sashForm;
	}

	@Override
	public Composite getComposite() {
		return sashForm;
	}

}