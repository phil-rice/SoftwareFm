package org.softwareFm.eclipse.mysoftwareFm;

import org.eclipse.swt.widgets.Table;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.eclipse.user.ProjectFixture;
import org.softwareFm.eclipse.user.ProjectTimeGetterFixture;
import org.softwareFm.eclipse.user.UserMock;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.SwtTest;
import org.softwareFm.swt.swt.Swts;

public class MyDetailsTest extends SwtTest {

	private final IProjectTimeGetter timeGetter = new ProjectTimeGetterFixture();
	private final String cryptoKey = Crypto.makeKey();
	private final String softwareFmId = "someSoftwareFmId";
	private final String email = "someEmail";
	private CardConfig cardConfig;

	public void test() {
		IUser user = new UserMock(cryptoKey, softwareFmId, LoginConstants.emailKey, email, LoginConstants.monikerKey, "someMoniker");
		String projectCryptoKey = user.getUserProperty(softwareFmId, cryptoKey, SoftwareFmConstants.projectCryptoKey);
		IProject project = new ProjectFixture(softwareFmId, projectCryptoKey);
		UserData userData = new UserData(email, softwareFmId, cryptoKey);
		MyDetails myDetails = new MyDetails(shell, cardConfig, userData, user, project, timeGetter);
		checkUserDetails(myDetails);
		checkProjectDetails(myDetails);
	}

	private void checkProjectDetails(MyDetails myDetails) {
		Table projectTable = (Table) Swts.getDescendant(myDetails.getControl(), 1);
		Swts.checkColumns(projectTable, "Group ID", "Artifact ID", "Month1", "Month2", "Month3");

		assertEquals(7, projectTable.getItemCount());
		Swts.checkRow(projectTable, 0, "group1", "artifact11", "4", "4", "4");
		Swts.checkRow(projectTable, 1, "group1", "artifact12", "3", "0", "3");
		Swts.checkRow(projectTable, 2, "group1", "artifact13", "0", "3", "0");
		Swts.checkRow(projectTable, 3, "group2", "artifact21", "2", "2", "0");
		Swts.checkRow(projectTable, 4, "group2", "artifact22", "3", "3", "0");
		Swts.checkRow(projectTable, 5, "group3", "artifact31", "0", "0", "2");
		Swts.checkRow(projectTable, 6, "group3", "artifact32", "0", "0", "3");
	}

	protected void checkUserDetails(MyDetails myDetails) {
		Table userTable = (Table) Swts.getDescendant(myDetails.getControl(), 0);
		assertEquals(2, userTable.getColumnCount());

		assertEquals(3, userTable.getItemCount());
		Swts.checkRow(userTable, 0, "email", "someEmail");
		Swts.checkRow(userTable, 1, "moniker", "someMoniker");
		Swts.checkRow(userTable, 2, "softwareFmId", "someSoftwareFmId");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = CardDataStoreFixture.syncCardConfig(display);
	}
}
