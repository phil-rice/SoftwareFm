/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.Map;

import org.softwareFm.crowdsource.api.server.AbstractCallProcessor;
import org.softwareFm.crowdsource.api.server.IForgottonPasswordMailer;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.server.ISaltProcessor;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class ForgottonPasswordProcessor extends AbstractCallProcessor {

	private final IForgottonPasswordMailer forgottonPasswordMailer;
	private final ISaltProcessor saltProcessor;

	public ForgottonPasswordProcessor(ISaltProcessor saltProcessor, IForgottonPasswordMailer forgottonPasswordMailer) {
		super(CommonConstants.POST, LoginConstants.forgottonPasswordPrefix);
		this.saltProcessor = saltProcessor;
		this.forgottonPasswordMailer = forgottonPasswordMailer;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String salt = Strings.nullSafeToString(parameters.get(LoginConstants.sessionSaltKey));
		String email = Strings.nullSafeToString(parameters.get(LoginConstants.emailKey));
		saltProcessor.invalidateSalt(salt);
		String magicString = forgottonPasswordMailer.process(email);
		System.out.println("Magic String: " + magicString);
		return IProcessResult.Utils.processString("");
	}

}