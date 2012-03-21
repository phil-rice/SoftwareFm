/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.doers.internal;

import java.util.Set;
import java.util.UUID;

import org.softwareFm.crowdsource.api.server.ISaltProcessor;
import org.softwareFm.crowdsource.utilities.collections.Sets;

public class SaltProcessor implements ISaltProcessor{

	private final Set<String> legal = Sets.newSet();
	@Override
	public String makeSalt() {
		String salt = UUID.randomUUID().toString();
		legal.add(salt);
		return salt;
	}


	@Override
	public boolean invalidateSalt(String salt) {
		return legal.remove(salt);
	}

}