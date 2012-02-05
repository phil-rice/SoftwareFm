package org.softwareFm.swt;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.eclipse.mysoftwareFm.MyDetails;
import org.softwareFm.eclipse.user.ProjectFixture;
import org.softwareFm.eclipse.user.ProjectTimeGetterFixture;
import org.softwareFm.eclipse.user.UserMock;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;

public class MyDetailsMain {
	public static void main(String[] args) {
		Swts.Show.display(MyDetails.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				String cryptoKey = Crypto.makeKey();
				Map<String, Object> userDetails = Maps.makeImmutableMap("some", "user details");
				String email = "someEmail";
				UserMock user = new UserMock(cryptoKey, userDetails, LoginConstants.emailKey, email, LoginConstants.monikerKey, "someMoniker");
				ProjectFixture project = new ProjectFixture(userDetails);
				UserData userData = new UserData(email, "someSoftwarefmId", cryptoKey);
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				return new MyDetails(from, cardConfig, userData, user, project, new ProjectTimeGetterFixture()).getComposite();
			}
		});
	}
}
