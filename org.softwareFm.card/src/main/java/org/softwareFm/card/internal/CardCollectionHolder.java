package org.softwareFm.card.internal;

import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.IDetailsFactoryCallback;
import org.softwareFm.card.internal.details.IGotDataCallback;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CardCollectionHolder implements IHasComposite {

	String rootUrl;
	private final CardCollectionHolderComposite content;

	public static class CardCollectionHolderLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			CardConfig cardConfig = ((CardCollectionHolderComposite) composite).cardConfig;
			Control[] children = composite.getChildren();
			int noOfChildren = children.length;
			if (wHint == SWT.DEFAULT)
				if (hHint == SWT.DEFAULT) {
					int idealHeight = 0;
					int idealWidth = 0;
					for (Control control : children)
						idealHeight = Math.max(idealHeight, control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
					idealWidth = heightToWidth(cardConfig, idealHeight) * noOfChildren;
					return new Point(idealWidth, idealHeight);
				} else {
					int width = heightToWidth(cardConfig, hHint) * noOfChildren;
					return new Point(width, hHint);
				}
			else if (hHint == SWT.DEFAULT) {
				int height = widthToHeight(cardConfig, wHint);
				return new Point(wHint, height);
			} else {
				int heightForwHint = widthToHeight(cardConfig, wHint / noOfChildren);
				int clippedheight = Math.min(heightForwHint, hHint);
				int widthForClippedHeight = heightToWidth(cardConfig, clippedheight);
				int width = Math.min(wHint / noOfChildren, widthForClippedHeight);
				int height = widthToHeight(cardConfig, width);
				Point result = new Point(width, height);
				return result;
			}
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			CardConfig cardConfig = ((CardCollectionHolderComposite) composite).cardConfig;
			Rectangle clientArea = composite.getClientArea();
			System.out.println("CCH " + " " + Swts.boundsUpToShell(composite) + " clientAreas: " + Swts.clientAreasUpToShell(composite));
			int height = clientArea.height;
			int width = heightToWidth(cardConfig, height);
			int x = clientArea.x;
			for (Control control : composite.getChildren()) {
				control.setLocation(x, clientArea.y);
				control.setSize(width, height);
				if (control instanceof Composite)
					((Composite) control).layout();
				x += width;
			}

		}

		private int widthToHeight(CardConfig cardConfig, int wHint) {
			return wHint * cardConfig.defaultHeightWeight / cardConfig.defaultWidthWeight;
		}

		private int heightToWidth(CardConfig cardConfig, int hHint) {
			return hHint * cardConfig.defaultWidthWeight / cardConfig.defaultHeightWeight;
		}

	}

	public static class CardCollectionHolderComposite extends HoldsCardHolder {

		private String key;
		private Object value;

		public CardCollectionHolderComposite(Composite parent, CardConfig cardConfig) {
			super(parent, SWT.NULL, cardConfig);
		}


		@SuppressWarnings("unchecked")
		protected Future<?> setKeyValue(final String rootUrl, String key, Object value, final IGotDataCallback callback) {
			if (isDisposed())
				return null;
			this.key = key;
			this.value = value;
			Swts.removeAllChildren(this);
			if (value instanceof Map<?, ?>) {
				Map<String, ?> map = Maps.sortByKey((Map<String, ?>) value, cardConfig.comparator);
				for (final Map.Entry<String, ?> childEntry : map.entrySet()) {
					if (childEntry.getValue() instanceof Map<?, ?>) {
						String detailUrl = rootUrl + "/" + childEntry.getKey();
						String title = childEntry.getKey();
						makeCardHolder(detailUrl, title);
					}
				}
				callback.gotData(CardCollectionHolderComposite.this);
			}
			return null;
		}

	}

	public CardCollectionHolder(Composite parent, CardConfig cardConfig) {
		content = new CardCollectionHolderComposite(parent, cardConfig);
	}

	public void setKeyValue(final String rootUrl, String key, Object value, IDetailsFactoryCallback callback) {
		this.rootUrl = rootUrl;
		content.setKeyValue(rootUrl, key, value, callback);
		content.addCardSelectedListener(callback);
	}

	public String getRootUrl() {
		return rootUrl;
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public CardConfig getCardConfig() {
		return content.cardConfig;
	}

	public String getKey() {
		return content.key;
	}

	public Object getValue() {
		return content.value;
	}

	public static void main(String[] args) {
		Show.displayNoLayout(CardCollectionHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				Display display = from.getDisplay();
				final CardConfig cardConfig = new BasicCardConfigurator().configure(display, CardDataStoreFixture.syncCardConfig(display));
				IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "navBar.prev.title");
				final CardCollectionHolder cardCollectionHolder = new CardCollectionHolder(from, cardConfig);
				cardCollectionHolder.setKeyValue(CardDataStoreFixture.url, "stuff", Maps.makeMap(CardDataStoreFixture.dataIndexedByUrlFragment), IDetailsFactoryCallback.Utils.resizeAfterGotData());
				return cardCollectionHolder.getComposite();
			}
		});
	}
}
