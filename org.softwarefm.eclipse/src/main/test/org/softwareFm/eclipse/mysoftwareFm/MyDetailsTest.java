package org.softwareFm.eclipse.mysoftwareFm;

import java.util.Map;

import org.eclipse.swt.widgets.Table;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.eclipse.user.ProjectFixture;
import org.softwareFm.eclipse.user.ProjectTimeGetterFixture;
import org.softwareFm.eclipse.user.UserMock;
import org.softwareFm.swt.swt.SwtTest;
import org.softwareFm.swt.swt.Swts;

public class MyDetailsTest extends SwtTest {

	private final IProjectTimeGetter timeGetter = new ProjectTimeGetterFixture();
	private final String cryptoKey = Crypto.makeKey();
	private final Map<String, Object> userDetails = Maps.makeImmutableMap("some", "user details");

	public void test() {
		IUser user = new UserMock(cryptoKey, userDetails, LoginConstants.emailKey, "someEmail", LoginConstants.monikerKey, "someMoniker");
		IProject project = new ProjectFixture(userDetails);
		MyDetails myDetails = new MyDetails(shell, cryptoKey, user, project, timeGetter, userDetails);
		checkUserDetails(myDetails);
		checkProjectDetails(myDetails);
	}

	private void checkProjectDetails(MyDetails myDetails) {
		Table projectTable = (Table) Swts.getDescendant(myDetails.getControl(), 1);
		Swts.checkColumns(projectTable, "Group ID", "Artifact ID", "month1", "month2", "month3");

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

		assertEquals(2, userTable.getItemCount());
		Swts.checkRow(userTable, 0, "email", "someEmail");
		Swts.checkRow(userTable, 1, "moniker", "someMoniker");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
}
