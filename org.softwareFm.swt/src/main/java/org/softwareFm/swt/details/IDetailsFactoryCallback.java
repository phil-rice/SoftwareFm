/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.details;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.card.ICardSelectedListener;
import org.softwareFm.swt.dataStore.IAfterEditCallback;
import org.softwareFm.swt.dataStore.IMutableCardDataStore;
import org.softwareFm.swt.dataStore.IUpdateDataStore;
import org.softwareFm.swt.details.internal.IGotDataCallback;
import org.softwareFm.swt.swt.Swts.Size;

/**
 * When a detail is shown there are a lot of potential callbacks/interfaces, so this aggregates them together.<br />
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