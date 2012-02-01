package org.softwareFm.eclipse.constants;
import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.client.http.api.IClientBuilder;
import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.collections.explorer.internal.MySoftwareFm;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;


public class MySoftwareFmDemo {
	public static void main(String[] args) {
		final IServiceExecutor service = IServiceExecutor.Utils.defaultExecutor();
		final IClientBuilder client = IHttpClient.Utils.builder("localhost", CommonConstants.testPort);
		File home = new File(System.getProperty("user.home"));
		final File localRoot = new File(home, ".sfm");
		final File remoteRoot = new File(home, ".sfm");
		IFunction1<Map<String, Object>, String> cryptoFn = Functions.expectionIfCalled();
		Callable<String> cryptoGenerator = Callables.makeCryptoKey();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		IGitOperations gitOperations = IGitOperations.Utils.gitOperations(remoteRoot);
		IProcessCall processCall = IProcessCall.Utils.softwareFmProcessCallWithoutMail(dataSource, gitOperations, cryptoFn, cryptoGenerator, localRoot, Callables.uuidGenerator());
		ISoftwareFmServer server = ISoftwareFmServer.Utils.testServerPort(processCall, ICallback.Utils.rethrow());
		try {
			Swts.Show.display(MySoftwareFm.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(from.getDisplay());
					MySoftwareFm mySoftwareFm = new MySoftwareFm(from, cardConfig, ILoginStrategy.Utils.softwareFmLoginStrategy(from.getDisplay(), service, client));
					mySoftwareFm.start();
					return mySoftwareFm.getComposite();
				}
			});
		} finally {
			server.shutdown();
			client.shutdown();
			service.shutdown();
		}
	}
}
