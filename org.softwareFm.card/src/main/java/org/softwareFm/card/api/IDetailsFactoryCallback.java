package org.softwareFm.card.api;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.internal.details.IGotDataCallback;
import org.softwareFm.card.internal.details.IUpdateDataStore;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.utilities.maps.Maps;

/** When a detail is shown there are a lot of potential callbacks/interfaces, so this aggregates them together.<br />
 * <ul>
 * <li>{@link ICardSelectedListener} is the listener to be called if the detail creates one or more cards that are then clicked on (e.g. the detail shows a collection of cards )
 * <li>{@link IGotDataCallback} must be called when the control is fit to be displayed. So for example TextEditorDetailAdder calls it as soon as the control is available, but when showing a card collection, it is not called until the count of the cards is known
 * <li>{@link IAfterEditCallback} is called if the detail is an editor, and actually makes changes on the server
 * <li>{@link IUpdateDataStore} is the mechanism used to update the server
 * */
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
				public void cardSelected(String cardUrl) {
				}

				@Override
				public void updateDataStore(IMutableCardDataStore store, String url, String key, Object value) {
				}
			};
		}

		public static IDetailsFactoryCallback resizeAfterGotData() {
			return new IDetailsFactoryCallback() {
				@Override
				public void cardSelected(String cardUrl) {
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
