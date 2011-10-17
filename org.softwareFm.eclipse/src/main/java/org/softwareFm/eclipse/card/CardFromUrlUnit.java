package org.softwareFm.eclipse.card;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
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
import org.softwareFm.utilities.callbacks.ICallback;

public class CardFromUrlUnit {
	public static void main(String[] args) throws IOException {
		final File root = new File("../org.softwareFm.card/src/test/resources/org/softwareFm/card/url").getCanonicalFile();
		final ICardFactoryWithAggregateAndSort cardFactory = ICardFactory.Utils.cardFactoryWithAggregateAndSort();
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacard();
		try {
			Swts.xUnit(CardFromUrlUnit.class.getSimpleName(), root, "sfm", new ISituationListAndBuilder<IHasComposite>() {

				private Listener listener;

				@Override
				public void selected(IHasComposite hasControl, String context, final Object value) throws Exception {
					String url = value.toString();
					final Composite content = hasControl.getComposite();
					if(listener != null)
						content.removeListener(SWT.Resize, listener);
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
					ICard card = cardFactory.makeCard(content, cardDataStore, url, new ICallback<ICard>(){
						@Override
						public void process(ICard t) throws Exception {
							Rectangle clientArea = content.getClientArea();
							Composite cardComposite =t.getComposite();
							Point size = cardComposite.computeSize(clientArea.width, clientArea.height);
							cardComposite.setSize(size);
							cardComposite.layout();
						}});
					final Composite cardComposite = card.getComposite();
					listener = new Listener() {
						@Override
						public void handleEvent(Event event) {
							Rectangle clientArea = content.getClientArea();
							Point size = cardComposite.computeSize(clientArea.width, clientArea.height);
							cardComposite.setSize(size);
							cardComposite.layout();
						}
					};
					cardComposite.getParent().addListener(SWT.Resize, listener);
					card.addLineSelectedListener(new ILineSelectedListener() {
						@Override
						public void selected(KeyValue keyValue, ILine line) {
							System.out.println(keyValue);
						}
					});
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
