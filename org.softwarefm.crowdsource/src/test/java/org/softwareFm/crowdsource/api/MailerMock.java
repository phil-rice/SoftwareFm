/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api;

import java.util.List;

import org.softwareFm.crowdsource.api.server.IMailer;
import org.softwareFm.crowdsource.utilities.collections.Lists;

public class MailerMock implements IMailer {

	public final List<String> froms = Lists.newList();
	public final List<String> tos = Lists.newList();
	public final List<String> subjects = Lists.newList();
	public final List<String> messages = Lists.newList();

	@Override
	public void mail(String from, String to, String subject, String message) {
		froms.add(from);
		tos.add(to);
		subjects.add(subject);
		messages.add(message);
	}

}