package org.softwareFm.card.api;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.internal.details.IGotDataCallback;
import org.softwareFm.card.internal.details.IUpdateDataStore;
import org.softwareFm.display.swt.Swts.Size;
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
					Size.setSizeFromClientArea(control);
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