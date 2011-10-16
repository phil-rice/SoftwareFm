package org.softwareFm.eclipse.card;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardFactoryWithAggregateAndSort;
import org.softwareFm.card.api.ILine;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;

public class CardFromUrlUnit {
	public static void main(String[] args) throws IOException {
		final File root = new File("../org.softwareFm.card/src/test/resources/org/softwareFm/card/url").getCanonicalFile();
		final ICardFactoryWithAggregateAndSort cardFactory = ICardFactory.Utils.cardFactoryWithAggregateAndSort();
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacard();
		try {
			Swts.xUnit(CardFromUrlUnit.class.getSimpleName(), root, "sfm", new ISituationListAndBuilder<IHasComposite>() {

				@Override
				public void selected(IHasComposite hasControl, String context, final Object value) throws Exception {
					String url = value.toString();
					Composite content = hasControl.getComposite();
					Swts.removeAllChildren(content);
					ICardDataStore cardDataStore = new ICardDataStore() {
						@Override
						public Future<?> processDataFor(final String url, final ICardDataStoreCallback callback) {
							return facard.get(url, new IRepositoryFacardCallback() {
								@Override
								public void process(IResponse response, Map<String, Object> data) {
									callback.process(url, data);
								}
							});
						}
					};
					ICard card = cardFactory.makeCard(content, cardDataStore, url);
					card.addLineSelectedListener(new ILineSelectedListener() {
						@Override
						public void selected(KeyValue keyValue, ILine line) {
							System.out.println(keyValue);
						}
					});
					Swts.layoutAsString(content);
				}

				@Override
				public IHasComposite makeChild(Composite parent) throws Exception {
					final Group result = new Group(parent, SWT.NULL);
					result.setText("parent");
					Swts.resizeMeToParentsSize(result);
					return new IHasComposite() {

						@Override
						public Control getControl() {
							return result;
						}

						@Override
						public Composite getComposite() {
							return result;
						}
					};
				}
			});
		} finally {
			facard.shutdown();
		}
	}
}
