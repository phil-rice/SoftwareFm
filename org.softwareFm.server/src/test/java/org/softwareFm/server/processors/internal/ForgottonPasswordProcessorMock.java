/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.server.processors.IForgottonPasswordMailer;

public class ForgottonPasswordProcessorMock implements IForgottonPasswordMailer {

	public final List<String> emails = Lists.newList();

	private String errorMessage;

	public ForgottonPasswordProcessorMock(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String process(String email) {
		if (errorMessage != null)
			throw new RuntimeException(errorMessage);
		emails.add(email);
		return "someMagicString";
	}

}