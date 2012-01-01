package org.softwareFm.server;

import java.util.concurrent.Future;

import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.server.internal.IGetCallback;

public interface ISoftwareFmClient extends IRepositoryFacard {

	/** The callback accesses the repository base or null if not found. The future is just to allow get to be called */
	Future<GetResult> findRepositoryBaseOrAboveRepositoryData(String url, IGetCallback callback);

}
