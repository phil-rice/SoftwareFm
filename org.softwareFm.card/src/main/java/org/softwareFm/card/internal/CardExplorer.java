package org.softwareFm.card.internal;

import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.IDetailFactory;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.repositoryFacardConstants.RepositoryFacardConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;

public class CardExplorer implements IHasComposite {

	private final CardExplorerComposite content;

	static class CardExplorerComposite extends SashForm {
		final CardHolder left;
		final SashForm right;
		final ScrolledComposite detail;
		final ScrolledComposite comments;
		private Listener detailResizeListener;

		public CardExplorerComposite(final Composite parent, final ICardFactory cardFactory, final IDetailFactory detailFactory, final String initialUrl) {
			super(parent, SWT.HORIZONTAL);
			left = new CardHolder(this, "");
			right = new SashForm(this, SWT.VERTICAL);
			right.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			detail = new ScrolledComposite(right, SWT.H_SCROLL);
			detail.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));

			comments = new ScrolledComposite(right, SWT.H_SCROLL);
			this.setWeights(new int[] { 2, 3 });
			right.setWeights(new int[] { 1, 2 });
			selectUrl(parent, cardFactory, detailFactory, initialUrl);
		}

		private void selectUrl(final Composite parent, final ICardFactory cardFactory, final IDetailFactory detailFactory, final String url) {
			cardFactory.makeCard(left.getComposite(), SWT.FULL_SELECTION, true, url, new ICallback<ICard>() {
				@Override
				public void process(ICard card) throws Exception {
					System.out.println("In callback");
					Swts.resizeMeToParentsSize(card.getControl());
					card.addLineSelectedListener(new ILineSelectedListener() {

						@Override
						public void selected(KeyValue keyValue) {
							Swts.removeOldResizeListener(detail, detailResizeListener);
							Swts.removeAllChildren(detail);
							detail.setContent(null);
							final Composite madeDetail = detailFactory.makeDetail(detail, SWT.FULL_SELECTION, cardFactory, new ICardSelectedListener() {

								@Override
								public void mouseDown(ICard card, MouseEvent e) {
									selectUrl(parent, cardFactory, detailFactory, card.url());
								}
							}, url, keyValue);
							Rectangle clientArea = detail.getClientArea();
							Point size = madeDetail.computeSize(SWT.DEFAULT, clientArea.height);
							madeDetail.setSize(size);
							detail.setContent(madeDetail);

							detailResizeListener = new Listener() {
								@Override
								public void handleEvent(Event event) {
									Rectangle clientArea = detail.getClientArea();
									Point size = madeDetail.computeSize(SWT.DEFAULT, clientArea.height);
									madeDetail.setSize(size);
								}
							};
							detail.addListener(SWT.Resize, detailResizeListener);
							// detail.setSize(detail.getSize());
							// detail.layout();
							detail.layout();
							detail.getParent().layout();
							detail.getParent().redraw();
							System.out.println("Detail: " + detail.getClientArea() + " Made: " + madeDetail.getSize());
						}
					});
					left.setCard(card);
					System.out.println("End callback");
				}
			});
		}

	}

	public CardExplorer(Composite parent, final ICardFactory cardFactory, IDetailFactory detailFactory, String initialUrl) {
		content = new CardExplorerComposite(parent, cardFactory, detailFactory, initialUrl);

	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public static void main(String[] args) {
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacard();
		final String url = "/softwareFm/repository/org";
		try {
			final ICardDataStore cardDataStore = new ICardDataStore() {
				@Override
				public <T>Future<T> processDataFor(final String url, final ICardDataStoreCallback<T> callback) {
					Future future = facard.get(url, new IRepositoryFacardCallback() {
						@Override
						public void process(IResponse response, Map<String, Object> data) {
							try {
								if (RepositoryFacardConstants.okStatusCodes.contains(response.statusCode()))
									callback.process(url, data);
								else {
									System.out.println(response.asString());
									callback.noData(url);
								}
							} catch (Exception e) {
								throw WrappedException.wrap(e);
							}
						}
					});
					return future;
				}
			};
			Swts.display(CardExplorer.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory(cardDataStore, "jcr:primaryType");
					CardExplorer cardExplorer = new CardExplorer(from, cardFactory, new DetailFactory(), url);
					return cardExplorer.getComposite();
				}
			});
		} finally {
			facard.shutdown();
		}
	}
}
