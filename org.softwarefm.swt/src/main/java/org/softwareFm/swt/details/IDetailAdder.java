/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.details;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.details.internal.CollectionItemDetailAdder;
import org.softwareFm.swt.details.internal.CollectionsDetailAdder;
import org.softwareFm.swt.details.internal.EditorDetailAdder;
import org.softwareFm.swt.details.internal.ListDetailAdder;
import org.softwareFm.swt.editors.internal.TextViewerDetailAdder;

public interface IDetailAdder {

	IHasControl add(Composite parentComposite, IContainer container, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback);

	public static class Utils {
		public static IDetailAdder collectionItem() {
			return new CollectionItemDetailAdder();
		}

		public static IDetailAdder collections() {
			return new CollectionsDetailAdder();
		}

		public static IDetailAdder editor() {
			return new EditorDetailAdder();
		}

		public static IDetailAdder list() {
			return new ListDetailAdder();
		}

		public static IDetailAdder textView() {
			return new TextViewerDetailAdder();
		}
	}

}