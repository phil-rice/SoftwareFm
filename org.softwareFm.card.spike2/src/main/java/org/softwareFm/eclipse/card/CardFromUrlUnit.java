package org.softwareFm.eclipse.card;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.composites.HasComposite;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;

public class CardFromUrlUnit {
	public static void main(String[] args) throws IOException {
		final ICardFactory cardFactory = ICardFactory.Utils.cardFactoryWithAggregateAndSort();
		final File root = new File("../org.softwareFm.card/src/test/resources/org/softwareFm/card/url").getCanonicalFile();
		String extension = "sfm";
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacard();
		Swts.xUnit(CardUnit.class.getSimpleName(), root, extension, new ISituationListAndBuilder<IHasComposite>() {


			@Override
			public void selected(IHasComposite hasControl, String context, Object value) throws Exception {
				Composite content = hasControl.getComposite();
				Swts.removeAllChildren(content);

				ICardDataStore cardDataStore = new ICardDataStore() {
					@Override
					public Future<?> processDataFor(final String url, final ICardDataStoreCallback callback) {
						return facard.get(url, new IRepositoryFacardCallback() {
							@Override
							public void process(IResponse response, Map<String, Object> data) throws Exception {
								callback.process(url, data);
							}
						});
					}
				};
				final ICard card = cardFactory.makeCard(content, cardDataStore, value.toString());
				Swts.resizeMeToParentsSize(card.getControl());
				Swts.setSizeFromClientArea(hasControl, card);
			}

			@Override
			public IHasComposite makeChild(Composite parent) throws Exception {
				return new HasComposite(parent, SWT.NULL);
			}
		});

	}
}