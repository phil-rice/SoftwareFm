/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.api.user.UserMock;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.eclipse.mysoftwareFm.MyDetails;
import org.softwareFm.eclipse.user.ProjectFixture;
import org.softwareFm.eclipse.user.ProjectMock;
import org.softwareFm.eclipse.user.ProjectTimeGetterFixture;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;

public class MyDetailsMain {
	public static void main(String[] args) {
		Swts.Show.display(MyDetails.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				String projectCryptoKey = Crypto.makeKey();
				String cryptoKey = projectCryptoKey;
				String softwareFmId = "someSoftwarefmId";
				String email = "someEmail";
				UserMock user = new UserMock(cryptoKey, softwareFmId, LoginConstants.emailKey, email, LoginConstants.monikerKey, "someMoniker");
				ProjectMock project = ProjectFixture.project1(softwareFmId, projectCryptoKey);
				UserData userData = new UserData(email, softwareFmId, cryptoKey);
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				return new MyDetails(from, cardConfig, userData, user, project, new ProjectTimeGetterFixture()).getComposite();
			}
		});
	}
}