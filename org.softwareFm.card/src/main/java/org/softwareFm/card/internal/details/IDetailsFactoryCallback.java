package org.softwareFm.card.internal.details;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.IMutableCardDataStore;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.maps.Maps;

public interface IDetailsFactoryCallback extends ICardSelectedListener, IGotDataCallback, IAfterEditCallback, IUpdateDataStore {

	public static class Utils {
		public static IDetailsFactoryCallback noCallback() {
			return new IDetailsFactoryCallback() {

				@Override
				public void afterEdit(String url) {
				}

				@Override
				public void gotData(Control control) {
				}

				@Override
				public void cardSelected(ICard card) {
				}

				@Override
				public void updateDataStore(IMutableCardDataStore store, String url, String key, Object value) {
				}
			};
		}

		public static IDetailsFactoryCallback resizeAfterGotData() {
			return new IDetailsFactoryCallback() {
				@Override
				public void cardSelected(ICard card) {
				}

				@Override
				public void gotData(Control control) {
					Swts.setSizeFromClientArea(control);
				}

				@Override
				public void afterEdit(String url) {
				}

				@Override
				public void updateDataStore(IMutableCardDataStore store, String url, String key, Object value) {
					store.put(url, Maps.<String, Object> makeMap(key, value), this);
				}
			};
		}
	}
}
