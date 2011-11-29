/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.explorer.eclipse;

import java.util.Map;

import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.jdtBinding.api.BindingRipperResult;

public class ExplorerView extends AbstractExplorerView {
	@Override
	protected String makeUrl(BindingRipperResult ripperResult, final CardConfig cardConfig, Map<String, Object> result) {
		IUrlGenerator cardUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.artifactUrlKey);
		String artifactUrl = cardUrlGenerator.findUrlFor(result);
		return artifactUrl;
	}

}