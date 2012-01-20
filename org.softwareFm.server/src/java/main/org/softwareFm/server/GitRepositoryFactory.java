package org.softwareFm.server;

import java.io.File;

import javax.sql.DataSource;

import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.server.internal.GitRepositoryFacard;
import org.softwareFm.server.internal.ImportGitRepositoryFacard;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.services.IServiceExecutor;

public class GitRepositoryFactory {

	/** This is mainly for tests: It starts a softwareFm Server (which is shut down when the repository is shutdown) The remote git is accessed through the file system. */
	public static IRepositoryFacard forImport(File remoteRoot) {
		IGitServer remoteGitServer = IGitServer.Utils.gitServer(remoteRoot, "not used");
		return new ImportGitRepositoryFacard(remoteGitServer);

	}

	public static IRepositoryFacard gitLocalRepositoryFacardWithServer(DataSource dataSource, int port, File localRoot, File remoteRoot) {
		IGitServer remoteGitServer = IGitServer.Utils.gitServer(remoteRoot, "not used");
		final ISoftwareFmServer server = ISoftwareFmServer.Utils.server(port, 10, IProcessCall.Utils.softwareFmProcessCall(dataSource, remoteGitServer, remoteRoot), ICallback.Utils.sysErrCallback());
		IHttpClient client = IHttpClient.Utils.builder("localhost", port);
		return gitRepositoryFacard(client, localRoot, remoteRoot.getAbsolutePath(), new Runnable() {
			@Override
			public void run() {
				server.shutdown();
			}
		});

	}

	public static IRepositoryFacard gitRepositoryFacard(IHttpClient client, File localRoot, String remoteUriPrefix, final Runnable... onShutdown) {
		IGitServer localGit = IGitServer.Utils.gitServer(localRoot, remoteUriPrefix);
		IServiceExecutor serviceExecutor = IServiceExecutor.Utils.defaultExecutor();
		return new GitRepositoryFacard(client, serviceExecutor, localGit, ServerConstants.staleCacheTime, ServerConstants.staleAboveRepositoryCacheTime) {
			@Override
			public void shutdown() {
				super.shutdown();
				for (Runnable runnable : onShutdown)
					runnable.run();
			}
		};

	}

	public static IRepositoryFacard gitRepositoryFacard(IHttpClient httpClient, IServiceExecutor serviceExecutor, IGitServer localGit) {
		return new GitRepositoryFacard(httpClient, serviceExecutor, localGit, ServerConstants.staleCacheTime, ServerConstants.staleAboveRepositoryCacheTime);

	}
}
