package org.softwareFm.crowdsourced.softwarefm;
import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.collections.explorer.internal.MySoftwareFm;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.httpClient.api.IClientBuilder;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.runnable.Callables;
import org.softwareFm.utilities.services.IServiceExecutor;


public class MySoftwareFmDemo {
	public static void main(String[] args) {
		final IServiceExecutor service = IServiceExecutor.Utils.defaultExecutor();
		final IClientBuilder client = IHttpClient.Utils.builder("localhost", CommonConstants.testPort);
		File home = new File(System.getProperty("user.home"));
		final File localRoot = new File(home, ".sfm");
		final File remoteRoot = new File(home, ".sfm");
		IFunction1<Map<String, Object>, String> cryptoFn = Functions.expectionIfCalled();
		Callable<String> monthGetter = Callables.exceptionIfCalled();
		Callable<Integer> dayGetter = Callables.exceptionIfCalled();
		Callable<String> cryptoGenerator = Callables.makeCryptoKey();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		IGitOperations gitOperations = IGitOperations.Utils.gitOperations(remoteRoot);
		IProcessCall processCall = IProcessCall.Utils.softwareFmProcessCallWithoutMail(dataSource, gitOperations, cryptoFn, cryptoGenerator, localRoot, monthGetter, dayGetter, Callables.uuidGenerator());
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
