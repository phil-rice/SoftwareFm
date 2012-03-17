/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.mysoftwareFm;

import org.eclipse.swt.widgets.Table;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.eclipse.user.ProjectFixture;
import org.softwareFm.eclipse.user.ProjectTimeGetterFixture;
import org.softwareFm.eclipse.user.UserMock;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.DataComposite;
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
		IProject project = ProjectFixture.project1(softwareFmId, projectCryptoKey);
		UserData userData = new UserData(email, softwareFmId, cryptoKey);
		MyDetails myDetails = new MyDetails(shell, cardConfig, userData, user, project, timeGetter);
		checkProjectDetails(myDetails);
	}

	@SuppressWarnings("unchecked")
	private void checkProjectDetails(MyDetails myDetails) {
		DataComposite<Table> composite = (DataComposite<Table>) myDetails.getComposite();
		Table projectTable = composite.getEditor();
		Swts.checkColumns(projectTable, "Group ID", "Artifact ID", "Jan 2012", "Feb 2012", "Mar 2012");

		assertEquals(7, projectTable.getItemCount());
		Swts.checkRow(projectTable, 0, "group1", "artifact11", "4", "4", "4");
		Swts.checkRow(projectTable, 1, "group1", "artifact12", "3", "0", "3");
		Swts.checkRow(projectTable, 2, "group1", "artifact13", "0", "3", "0");
		Swts.checkRow(projectTable, 3, "group2", "artifact21", "2", "2", "0");
		Swts.checkRow(projectTable, 4, "group2", "artifact22", "3", "3", "0");
		Swts.checkRow(projectTable, 5, "group3", "artifact31", "0", "0", "2");
		Swts.checkRow(projectTable, 6, "group3", "artifact32", "0", "0", "3");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = CardDataStoreFixture.syncCardConfig(display);
	}
}