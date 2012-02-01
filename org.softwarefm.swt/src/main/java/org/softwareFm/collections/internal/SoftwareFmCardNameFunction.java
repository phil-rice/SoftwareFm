/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.internal;

import java.util.Map;

import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.swt.card.AbstractLineItemFunction;
import org.softwareFm.swt.card.ILineItemFunction;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.configuration.CardConfig;

public class SoftwareFmCardNameFunction extends AbstractLineItemFunction<String> {

	private final ILineItemFunction<String> delegate;

	public SoftwareFmCardNameFunction(ILineItemFunction<String> delegate) {
		this.delegate = delegate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String apply(CardConfig cardConfig, LineItem from) {
		if (from.value instanceof Map<?, ?>) {
			Map<Object, Object> map = (Map<Object, Object>) from.value;
			String resourceType = (String) map.get(CardConstants.slingResourceType);
			String cardNameKey = IResourceGetter.Utils.getOrNull(cardConfig.resourceGetterFn, resourceType, CardConstants.cardNameFieldKey);
			String name = (String) map.get(cardNameKey);
			if (name != null)
				return name;
		}
		return delegate.apply(cardConfig, from);
	}
}