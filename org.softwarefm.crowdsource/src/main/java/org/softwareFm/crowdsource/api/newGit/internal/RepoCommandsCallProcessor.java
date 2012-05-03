package org.softwareFm.crowdsource.api.newGit.internal;

import java.text.MessageFormat;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.crowdsource.api.server.ICallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.constants.GitConstants;
import org.softwareFm.crowdsource.constants.GitMessages;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;

public class RepoCommandsCallProcessor implements ICallProcessor {

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (CommonConstants.POST.equals(requestLine.getMethod())) {
			String command = (String) parameters.get(GitConstants.commandKey);
			if (command != null) {
				String rl = requestLine.getUri();
				String result;
				if (command.equals(GitConstants.appendCommand))
					result = processAppendCommand(rl, parameters);
				else if (command.equals(GitConstants.changeCommand))
					result = processChangeCommand(rl, parameters);
				else if (command.equals(GitConstants.deleteCommand))
					result = processDeleteCommand(rl, parameters);
				else if (command.equals(GitConstants.findRepoCommand))
					result = processRepoCommand(rl, parameters);
				else
					throw new IllegalArgumentException(MessageFormat.format(GitMessages.doNotUnderstandRepoCommand, command, rl, parameters));
				return IProcessResult.Utils.processString(result);
			}
		}
		return null;
	}

	private String processRepoCommand(String rl, Map<String, Object> parameters) {
		return null;
	}

	private String processDeleteCommand(String rl, Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	private String processChangeCommand(String rl, Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	private String processAppendCommand(String rl, Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

}
