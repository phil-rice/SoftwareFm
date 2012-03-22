package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.ICrowdSourcedReadWriteApi;
import org.softwareFm.crowdsource.api.ICrowdSourcedReaderApi;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.LocalConfig;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.git.IGitWriter;
import org.softwareFm.crowdsource.api.git.IRepoFinder;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.comments.internal.CommentsLocal;
import org.softwareFm.crowdsource.git.internal.GitLocal;
import org.softwareFm.crowdsource.git.internal.HttpGitWriter;
import org.softwareFm.crowdsource.git.internal.HttpRepoFinder;
import org.softwareFm.crowdsource.httpClient.IClientBuilder;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.membership.internal.UserMembershipReaderForLocal;
import org.softwareFm.crowdsource.user.internal.LocalGroupsReader;
import org.softwareFm.crowdsource.user.internal.LocalUserReader;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;

public class CrowdSourcedLocalApi extends AbstractCrowdSourcesApi {

	private final CrowdSourceLocalReadWriterApi readWriterApi;
	private final IClientBuilder httpClient;

	@SuppressWarnings("Look really hard to git operations...why is it needed when we have a git local")
	public CrowdSourcedLocalApi(LocalConfig localConfig) {
		readWriterApi = new CrowdSourceLocalReadWriterApi(IGitOperations.Utils.gitOperations(localConfig.root));
		@SuppressWarnings("really dont want this either")
		GitLocal gitLocal = new GitLocal(readWriterApi, localConfig.remoteGitPrefix, localConfig.autoRefreshPeriod);

		httpClient = IHttpClient.Utils.builderWithThreads(localConfig.host, localConfig.port, localConfig.workerThreads);
		IUrlGenerator userUrlGenerator = localConfig.userUrlGenerator;
		HttpRepoFinder repoFinder = new HttpRepoFinder(httpClient, localConfig.timeOutMs);
		CommentsLocal comments = new CommentsLocal(readWriterApi, gitLocal, localConfig.timeOutMs);
		LocalUserReader userReader = new LocalUserReader(readWriterApi, userUrlGenerator);
		HttpGitWriter gitWriter = new HttpGitWriter(httpClient);
		readWriterApi.registerReaderAndWriter(IGitLocal.class, gitLocal);
		readWriterApi.registerReaderAndWriter(IHttpClient.class, httpClient);
		readWriterApi.registerReader(IUserReader.class, userReader);
		readWriterApi.registerReader(IGroupsReader.class, new LocalGroupsReader(readWriterApi, localConfig.groupUrlGenerator));
		readWriterApi.registerReader(IUserMembershipReader.class, new UserMembershipReaderForLocal(readWriterApi, userUrlGenerator));
		readWriterApi.registerReaderAndWriter(IRepoFinder.class, repoFinder);
		readWriterApi.registerReader(IGitReader.class, gitLocal);
		readWriterApi.registerReadWriter(IGitWriter.class, gitWriter);
		readWriterApi.registerReader(ICommentsReader.class, comments);
		readWriterApi.registerReadWriter(IComments.class, comments);
		readWriterApi.registerReader(IUserReader.class, userReader);
		readWriterApi.registerReadWriter(IRepoFinder.class, repoFinder);
		localConfig.extraReaderWriterConfigurator.builder(readWriterApi, localConfig );
	}

	@Override
	public ICrowdSourcedReaderApi makeReader() {
		return readWriterApi;
	}

	@Override
	public ICrowdSourcedReadWriteApi makeReadWriter() {
		return readWriterApi;
	}

	@Override
	public ICrowdSourcedServer getServer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void shutdown() {
		httpClient.shutdown();
	}
}
