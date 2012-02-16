/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.details.internal;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.internal.CardConfigFillWithAspectRatioLayout;
import org.softwareFm.swt.card.internal.OneCardHolder;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.details.IDetailAdder;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.swt.Swts;

/** Treats as an item in a collection if it has a type, and isn't a collection itself */
public class CollectionItemDetailAdder implements IDetailAdder {

	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (value instanceof Map<?, ?>) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) value;
			Object object = map.get(CardConstants.slingResourceType);
			if (object != null && !CardConstants.collection.equals(object)) {// it
				String url = parentCard.url() + "/" + key;
				OneCardHolder result = new OneCardHolder(parentComposite, cardConfig, url, key, callback);
				result.getComposite().setLayout(new CardConfigFillWithAspectRatioLayout());
				Swts.Size.setSizeFromClientArea(result.getControl());
				return result;
			}
		}
		return null;
	}

}