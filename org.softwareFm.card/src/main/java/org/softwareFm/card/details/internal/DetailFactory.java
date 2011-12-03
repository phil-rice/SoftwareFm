/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.details.internal;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.details.IDetailAdder;
import org.softwareFm.card.details.IDetailFactory;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.display.composites.IHasControl;

public class DetailFactory implements IDetailFactory {

	private final List<IDetailAdder> detailAdders;

	public DetailFactory(List<IDetailAdder> detailAdders) {
		this.detailAdders = detailAdders;
	}

	@Override
	public IHasControl makeDetail(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (key == null)
			return null;
		for (IDetailAdder adder : detailAdders) {
			IHasControl result = adder.add(parentComposite, parentCard, cardConfig.withPopupMenuId(cardConfig.detailsPopupMenuId, null), key, value, callback);
			if (result != null)
				return result;
		}
		return null;
	}

}