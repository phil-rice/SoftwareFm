/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.server.processors.IPasswordResetter;

public class PasswordResetterMock implements IPasswordResetter {

	private final String password;
	public final List<String> magicStrings = Lists.newList();

	public PasswordResetterMock(String password) {
		this.password = password;
	}

	@Override
	public String reset(String magicString) {
		magicStrings.add(magicString);
		return password;
	}

}