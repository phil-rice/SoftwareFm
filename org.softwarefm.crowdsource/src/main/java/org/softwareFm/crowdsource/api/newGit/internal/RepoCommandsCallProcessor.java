package org.softwareFm.crowdsource.api.newGit.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.api.internal.Container;
import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.RepoLocation;
import org.softwareFm.crowdsource.api.newGit.exceptions.MissingParameterException;
import org.softwareFm.crowdsource.api.newGit.facard.ISecurityTokenChecker;
import org.softwareFm.crowdsource.api.newGit.facard.ISecurityTokenMaker;
import org.softwareFm.crowdsource.api.newGit.facard.SecurityToken;
import org.softwareFm.crowdsource.api.server.ICallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.constants.GitConstants;
import org.softwareFm.crowdsource.constants.GitMessages;
import org.softwareFm.crowdsource.constants.SecurityConstants;
import org.softwareFm.crowdsource.constants.SecurityMessages;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class RepoCommandsCallProcessor implements ICallProcessor {

	private final Container container;
	private final IUserCryptoAccess userCryptoAccess;
	private final ISecurityTokenChecker tokenChecker;

	public RepoCommandsCallProcessor(Container container, IUserCryptoAccess userCryptoAccess, ISecurityTokenChecker tokenChecker) {
		this.container = container;
		this.userCryptoAccess = userCryptoAccess;
		this.tokenChecker = tokenChecker;
	}

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

	private String processDeleteCommand(final String rl, final Map<String, Object> parameters) {
		return processJobWithSecurity(rl, parameters, new IFunction1<IRepoData, String>() {
			@Override
			public String apply(IRepoData from) throws Exception {
				checkParameters(parameters, CommonConstants.indexParameterName);
				int index = findIndex(parameters);
				from.delete(ISingleSource.Utils.raw(rl), index);
				from.setCommitMessage("Deleting " + rl + " index " + index);
				return "";
			}

		});
	}

	private String processChangeCommand(final String rl, final Map<String, Object> parameters) {
		return processJobWithSecurity(rl, parameters, new IFunction1<IRepoData, String>() {
			@Override
			public String apply(IRepoData from) throws Exception {
				checkParameters(parameters, CommonConstants.dataParameterName, CommonConstants.indexParameterName);
				String rawData = (String) parameters.get(CommonConstants.dataParameterName);
				int index = findIndex(parameters);
				Map<String, Object> data = Json.mapFromString(rawData);
				from.change(new RawSingleSource(rl), index, data);
				from.setCommitMessage("Changinging to " + rl + ", " + rawData);
				return "";
			}
		});
	}

	private String processAppendCommand(final String rl, final Map<String, Object> parameters) {
		return processJobWithSecurity(rl, parameters, new IFunction1<IRepoData, String>() {
			@Override
			public String apply(IRepoData from) throws Exception {
				checkParameters(parameters, CommonConstants.dataParameterName);
				String rawData = findData(parameters);
				Map<String, Object> data = Json.mapFromString(rawData);
				from.append(new RawSingleSource(rl), data);
				from.setCommitMessage("Appending to " + rl + ", " + rawData);
				return "";
			}

		});
	}

	private String processRepoCommand(final String rl, Map<String, Object> parameters) {
		return processJobWithoutSecurity(rl, parameters, new IFunction1<IRepoData, String>() {
			@Override
			public String apply(IRepoData from) throws Exception {
				RepoLocation repoLocation = from.findRepository(new RawSingleSource(rl));
				return repoLocation == null ? "" : repoLocation.rl;
			}
		});
	}

	private String processJobWithoutSecurity(final String rl, final Map<String, Object> parameters, final IFunction1<IRepoData, String> job) {
		return container.access(IRepoData.class, job).get();
	}

	private String processJobWithSecurity(final String rl, final Map<String, Object> parameters, final IFunction1<IRepoData, String> job) {
		return container.access(IRepoData.class, new IFunction1<IRepoData, String>() {
			@Override
			public String apply(IRepoData from) throws Exception {
				checkSecurity(from, rl, parameters);
				return job.apply(from);
			}

			@Override
			public String toString() {
				return job.toString();
			}
		}).get();
	}

	private void checkSecurity(IRepoData repoData, String rl, Map<String, Object> parameters) {
		checkParameters(parameters, LoginConstants.softwareFmIdKey, LoginConstants.emailKey, SecurityConstants.securityTokenKey, SecurityConstants.fileDigestKey);
		String userId = Strings.nullSafeToString(parameters.get(LoginConstants.softwareFmIdKey));
		String email = Strings.nullSafeToString(parameters.get(LoginConstants.emailKey));
		String crypto = userCryptoAccess.getCryptoForUser(userId);
		SecurityToken securityToken = ISecurityTokenMaker.Utils.fromMap(parameters);
		tokenChecker.validate(repoData, securityToken, new UserData(email, userId, crypto), rl);
	}

	private void checkParameters(Map<String, Object> parameters, String... criticalParameters) {
		List<String> missing = Lists.newList();
		for (String criticalParameter : criticalParameters)
			if (!parameters.containsKey(criticalParameter))
				missing.add(criticalParameter);
		if (missing.size() > 0)
			throw new MissingParameterException(SecurityMessages.missingCriticalParameter, missing, parameters);
	}

	private int findIndex(final Map<String, Object> parameters) {
		return Integer.parseInt((String) parameters.get(CommonConstants.indexParameterName));
	}

	private String findData(final Map<String, Object> parameters) {
		return (String) parameters.get(CommonConstants.dataParameterName);
	}
}
