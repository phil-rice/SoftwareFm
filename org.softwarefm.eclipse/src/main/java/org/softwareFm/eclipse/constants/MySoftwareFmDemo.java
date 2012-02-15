package org.softwareFm.eclipse.constants;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.client.http.api.IClientBuilder;
import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.processors.IMailer;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.ProcessCallParameters;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.internal.MySoftwareFm;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.swt.Swts;

public class MySoftwareFmDemo {
	public static void main(String[] args) {
		final IServiceExecutor service = IServiceExecutor.Utils.defaultExecutor();
		final IClientBuilder client = IHttpClient.Utils.builder("localhost", CommonConstants.testPort);
		File home = new File(System.getProperty("user.home"));
		final File remoteRoot = new File(home, ".sfm");
		Callable<String> cryptoGenerator = Callables.makeCryptoKey();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		IGitOperations gitOperations = IGitOperations.Utils.gitOperations(remoteRoot);
		Callable<String> softwareFmIdGenerator = Callables.uuidGenerator();
		IFunction1<Map<String, Object>, String> userCryptoFn = ICrowdSourcedServer.Utils.cryptoFn(dataSource);
		Map<String, Callable<Object>> defaultValues = Maps.newMap();
		ProcessCallParameters processCallParameters = new ProcessCallParameters(dataSource, gitOperations, cryptoGenerator, softwareFmIdGenerator, userCryptoFn, IMailer.Utils.noMailer(), defaultValues);
		IProcessCall processCall = IProcessCall.Utils.softwareFmProcessCall(processCallParameters, Functions.<ProcessCallParameters, IProcessCall[]> constant(new IProcessCall[0]));
		ICrowdSourcedServer server = ICrowdSourcedServer.Utils.testServerPort(processCall, ICallback.Utils.rethrow());
		try {
			Swts.Show.display(MySoftwareFm.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(from.getDisplay());
					IUserReader userReader = IUserReader.Utils.exceptionUserReader();
					MySoftwareFm mySoftwareFm = new MySoftwareFm(from, cardConfig, ILoginStrategy.Utils.softwareFmLoginStrategy(from.getDisplay(), service, client, UserData.blank(), ICallback.Utils.<UserData> sysoutCallback()), IShowMyData.Utils.sysout(), IShowMyGroups.Utils.sysoutShowMyGroups(), userReader);
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
