/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.explorer.eclipse;

import java.util.Map;

import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.ExpressionData;
import org.softwareFm.jdtBinding.api.IExpressionCategoriser;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.collections.explorer.Explorer;

public class SnippetView extends AbstractExplorerView {

	IExpressionCategoriser categoriser = IExpressionCategoriser.Utils.categoriser();

	@Override
	protected String makeUrl(BindingRipperResult ripperResult, final CardConfig cardConfig, Map<String, Object> data) {
		ExpressionData key = categoriser.categorise(ripperResult.expression);
		if (key == null)
			return null;
		String baseUrl = rootUrl + "/" + key.classKey;
		if (key.methodKey != null && key.methodKey.length() > 0) {
			String result = baseUrl + "/method/" + key.methodKey;
			return result;
		} else
			return baseUrl;
	}

	@Override
	protected void processNoData(CardConfig cardConfig, Explorer explorer, IResourceGetter resourceGetter, BindingRipperResult ripperResult) {
		process(cardConfig, explorer, ripperResult, Maps.stringObjectLinkedMap());
	}
}