/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.dataStore.ICardDataStore;
import org.softwareFm.swt.login.ILoginCallbacks;
import org.softwareFm.swt.login.ILoginDisplayStrategy;
import org.softwareFm.swt.login.ILoginStrategy;
import org.softwareFm.swt.login.IShowMessage;
import org.softwareFm.swt.login.internal.Signup;
import org.softwareFm.swt.swt.Swts;

public class SignupMain {
	public static void main(String[] args) {
		Swts.Show.display(Signup.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite parent) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.basicConfigurator().configure(parent.getDisplay(), new CardConfig(ICardFactory.Utils.noCardFactory(), ICardDataStore.Utils.mock()));
				String salt = "someSalt";
				return (Composite) new Signup(parent, cardConfig, salt, "initialEmail", ILoginStrategy.Utils.sysoutLoginStrategy(), //
						ILoginDisplayStrategy.Utils.sysoutDisplayStrategy(), //
						ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, IShowMessage.Utils.sysout())).getControl();
			}
		});
	}
}