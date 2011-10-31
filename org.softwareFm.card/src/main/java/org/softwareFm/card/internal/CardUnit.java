package org.softwareFm.card.internal;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
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
	private final CardHolder cardHolder;
	private final Text text;
	private final CardConfig cardConfig;

	public CardUnit(Composite parent, CardConfig cardConfig, String rootUrl) {
		this.cardConfig = cardConfig;
		sashForm = new SashForm(parent, SWT.VERTICAL);
		cardHolder = new CardHolder(sashForm, "loading", "title", cardConfig, rootUrl, null);
		text = new Text(sashForm, SWT.WRAP);
	}

	public static void main(String[] args) {
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		try {

			Swts.xUnit(CardUnit.class.getSimpleName(), new ISituationListAndBuilder<CardUnit>() {
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
					final ICardDataStore cardDataStore = new CardDataStoreForRepository(parent, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = new BasicCardConfigurator().configure(parent.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					CardUnit cardUnit = new CardUnit(parent, cardConfig, rootUrl);
					return cardUnit;
				}
			}, urls);
		} finally {
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
