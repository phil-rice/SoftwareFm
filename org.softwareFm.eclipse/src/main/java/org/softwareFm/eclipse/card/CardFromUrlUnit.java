package org.softwareFm.eclipse.card;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardFactoryWithAggregateAndSort;
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
		Swts.xUnit(CardFromUrlUnit.class.getSimpleName(), root, "sfm", new ISituationListAndBuilder<IHasComposite>() {

			@Override
			public void selected(IHasComposite hasControl, String context, final Object value) throws Exception {
				Composite content = hasControl.getComposite();
				Swts.removeAllChildren(content);
				String url = value.toString();
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
				cardFactory.makeCard(content, cardDataStore, url);
				System.out.println();
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
	}
}
