package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.server.processors.IMailer;

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
