package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
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
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;

public class CrowdSourcedLocalApi extends AbstractCrowdSourcesApi {

	private final CrowdSourceLocalReadWriterApi container;
	private final IClientBuilder httpClient;

	@SuppressWarnings("unchecked")
	public CrowdSourcedLocalApi(LocalConfig localConfig) {
		ITransactionManager manager = ITransactionManager.Utils.standard();
		container = new CrowdSourceLocalReadWriterApi(manager, IGitOperations.Utils.gitOperations(localConfig.root), localConfig.timeOutMs);
		httpClient = IHttpClient.Utils.builderWithThreads(localConfig.host, localConfig.port, localConfig.workerThreads);
		HttpGitWriter gitWriter = new HttpGitWriter(httpClient);
		GitLocal gitLocal = new GitLocal(container, gitWriter, localConfig.remoteGitPrefix, localConfig.autoRefreshPeriod);

		IUrlGenerator userUrlGenerator = localConfig.userUrlGenerator;
		HttpRepoFinder repoFinder = new HttpRepoFinder(httpClient, localConfig.timeOutMs);
		CommentsLocal comments = new CommentsLocal(container, gitLocal, localConfig.timeOutMs);
		LocalUserReader userReader = new LocalUserReader(container, userUrlGenerator);
		container.register(IGitLocal.class, gitLocal);
		container.register(IHttpClient.class, httpClient);
		container.register(IUserReader.class, userReader);
		container.register(IGroupsReader.class, new LocalGroupsReader(container, localConfig.groupUrlGenerator));
		container.register(IUserMembershipReader.class, new UserMembershipReaderForLocal(container, userUrlGenerator));
		container.register(IRepoFinder.class, repoFinder);
		container.register(IGitReader.class, gitLocal);
		container.register(IGitWriter.class, gitLocal);
		container.register(ICommentsReader.class, comments);
		container.register(IComments.class, comments);
		container.register(IUserReader.class, userReader);
		container.register(IRepoFinder.class, repoFinder);
		localConfig.extraReaderWriterConfigurator.builder(container, localConfig );
	}


	@Override
	public IContainer makeContainer() {
		return container;
	}
	
	@Override
	public IUserAndGroupsContainer makeUserAndGroupsContainer() {
		return container;
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
