package org.arc4eclipse.binding.repositoryFacard;

import java.util.Map;

import org.arc4eclipse.binding.path.BindingPathCalculator;
import org.arc4eclipse.httpClient.constants.HttpClientConstants;
import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.repositoryClient.paths.impl.PathCalculatorThin;
import org.arc4eclipse.repositoryFacard.IAspectToParameters;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.repositoryFacard.impl.AspectToParameters;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.IBinding;

public class JdtRepositoryAccessor {
	public static IRepositoryFacard<IPath, IBinding, IEntityType, Map<Object, Object>> makeFacard() {
		return makeFacard(HttpClientConstants.defaultHost, HttpClientConstants.defaultPort, HttpClientConstants.userName, HttpClientConstants.password);
	}

	public static IRepositoryFacard<IPath, IBinding, IEntityType, Map<Object, Object>> makeFacard(String hostName, int port, String userName, String password) {
		IAspectToParameters<IBinding, IEntityType, Map<Object, Object>> aspectToParameters = new AspectToParameters<IBinding, IEntityType>();
		return IRepositoryFacard.Utils.facard(aspectToParameters, hostName, port, userName, password, new BindingPathCalculator(new PathCalculatorThin()));
	}
}
