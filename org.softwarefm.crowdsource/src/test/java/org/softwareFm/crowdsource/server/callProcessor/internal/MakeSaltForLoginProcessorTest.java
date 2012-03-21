/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import org.apache.http.RequestLine;
import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class MakeSaltForLoginProcessorTest extends AbstractProcessCallTest<MakeSaltForLoginProcessor> {

	private SaltProcessorMock saltProcessor;

	public void testIgnoresGetsWithoutCommandPrefix() {
		checkIgnoresNoneGet();
		checkIgnores(CommonConstants.GET);
	}

	public void testCreatesSalt() throws Exception {
		RequestLine requestLine = makeRequestLine(CommonConstants.GET, "/" + LoginConstants.makeSaltPrefix);
		IProcessResult result = processor.process(requestLine, Maps.emptyStringObjectMap());
		String salt = Lists.getOnly(saltProcessor.createdSalts);
		checkStringResult(result, salt);
		assertNotNull(result);


	}

	@Override
	protected MakeSaltForLoginProcessor makeProcessor() {
		saltProcessor = new SaltProcessorMock();
		return new MakeSaltForLoginProcessor( saltProcessor);
	}

}