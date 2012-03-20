/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.constants;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.server.ICallProcessor;
import org.softwareFm.crowdsource.api.server.IMailer;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.httpClient.IClientBuilder;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.IUserDataManager;
import org.softwareFm.swt.explorer.internal.MySoftwareFm;
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
		ProcessCallParameters processCallParameters = new ProcessCallParameters(dataSource, gitOperations, cryptoGenerator, softwareFmIdGenerator, userCryptoFn, IMailer.Utils.noMailer(), defaultValues, JarAndPathConstants.urlPrefix);
		ICallProcessor processCall = ICallProcessor.Utils.softwareFmProcessCall(processCallParameters, Functions.<ProcessCallParameters, ICallProcessor[]> constant(new ICallProcessor[0]));
		ICrowdSourcedServer server = ICrowdSourcedServer.Utils.testServerPort(processCall, ICallback.Utils.rethrow());
		try {
			Swts.Show.display(MySoftwareFm.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(from.getDisplay());
					IUserReader userReader = IUserReader.Utils.exceptionUserReader();
					MySoftwareFm mySoftwareFm = new MySoftwareFm(from, cardConfig, ILoginStrategy.Utils.softwareFmLoginStrategy(from.getDisplay(), service, client), IShowMyData.Utils.sysout(), IShowMyGroups.Utils.sysoutShowMyGroups(), userReader, IUserDataManager.Utils.userDataManager());
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