/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.server.AbstractCallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.navigation.IRepoNavigation;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.json.Json;

public class NavigationCommandProcessor extends AbstractCallProcessor {

	private final IRepoNavigation repoNavigation;

	public NavigationCommandProcessor(IUserAndGroupsContainer container, IUserCryptoAccess cryptoAccess, IRepoNavigation repoNavigation) {
		super(CommonConstants.POST, CommonConstants.navigationPrefix);
		this.repoNavigation = repoNavigation;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		Map<String, List<String>> map = repoNavigation.navigationData(actualUrl, ICallback.Utils.<Map<String, List<String>>> noCallback()).get();
		return IProcessResult.Utils.processString(Json.toString(map));
	}

}