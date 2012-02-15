package org.softwareFm.softwareFmServer;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.future.Futures;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.AbstractJarToGroupArtifactVersion;
import org.softwareFm.eclipse.IGroupArtifactVersionCallback;

public class JarToGroupArtifactVersionOnServer extends AbstractJarToGroupArtifactVersion {


	private final IGitOperations gitOperations;

	public JarToGroupArtifactVersionOnServer(IUrlGenerator jarUrlGenerator, IGitOperations gitOperations) {
		super(jarUrlGenerator);
		this.gitOperations = gitOperations;
	}

	@Override
	protected Future<?> processMapFrom(IFileDescription fileDescription, IGroupArtifactVersionCallback callback) {
		Map<String, Object> map = gitOperations.getFile(fileDescription);
		replyTo(callback, map);
		return Futures.doneFuture(map);
	}

}
