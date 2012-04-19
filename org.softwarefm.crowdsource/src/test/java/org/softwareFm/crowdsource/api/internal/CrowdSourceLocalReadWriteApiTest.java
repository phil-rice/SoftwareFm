/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.git.IRepoFinder;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public class CrowdSourceLocalReadWriteApiTest extends AbstractCrowdReadWriterApiTest {

	public void testExtras() {
		IFunction1<IRepoFinder, Integer> repoFinderFn = Functions.<IRepoFinder> ensureSameParameters();
		getApi().makeContainer().access(IRepoFinder.class, repoFinderFn).get();
		getApi().makeContainer().access(IRepoFinder.class, repoFinderFn).get();
	}

	@Override
	protected ICrowdSourcedApi getApi() {
		return super.getLocalApi();
	}

}