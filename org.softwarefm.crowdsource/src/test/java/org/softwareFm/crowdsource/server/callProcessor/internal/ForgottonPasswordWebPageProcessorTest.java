/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.Map;

import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class ForgottonPasswordWebPageProcessorTest extends AbstractProcessCallTest<ForgottonPasswordProcessor> {

	private SaltProcessorMock saltProcessor;
	private ForgottonPasswordMailerMock forgottonPasswordMailer;
	private final String uri = "/" + LoginConstants.forgottonPasswordPrefix;

	public void testIgnoresNonePostsAndNonePrefix() {
		checkIgnoresNonePosts();
		checkIgnores(CommonConstants.POST);
		checkIgnores(CommonConstants.GET, uri);
	}

	public void testSendsEmailToMailer(){
		RequestLineMock requestLine = new RequestLineMock(CommonConstants.POST, uri);
		Map<String, Object> data = Maps.stringObjectMap(LoginConstants.emailKey, "someEmail", LoginConstants.sessionSaltKey, "");
		IProcessResult result = processor.process(requestLine, data);
		checkStringResult(result, "");
	}
	@Override
	protected ForgottonPasswordProcessor makeProcessor() {
		saltProcessor = new SaltProcessorMock();
		forgottonPasswordMailer = new ForgottonPasswordMailerMock("someMagicString");
		return new ForgottonPasswordProcessor(saltProcessor, forgottonPasswordMailer);
	}

}