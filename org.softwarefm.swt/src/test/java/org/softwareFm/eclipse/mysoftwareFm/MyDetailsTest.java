/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.mysoftwareFm;

import org.eclipse.swt.widgets.Table;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.IContainerBuilder;
import org.softwareFm.crowdsource.api.IExtraReaderWriterConfigurator;
import org.softwareFm.crowdsource.api.LocalConfig;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.jarAndClassPath.api.IProjectTimeGetter;
import org.softwareFm.jarAndClassPath.api.IUsageReader;
import org.softwareFm.jarAndClassPath.api.ProjectTimeGetterFixture;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.DataComposite;
import org.softwareFm.swt.swt.Swts;

public class MyDetailsTest extends AbstractMyGroupsIntegrationTest {

	private final String email = "someEmail";
	private CardConfig cardConfig;

	public void testMyDetails() {
		assertNotNull(IUserReader.Utils.getUserProperty(getServerApi().makeUserAndGroupsContainer(), softwareFmId0, userKey0, JarAndPathConstants.projectCryptoKey));
		UserData userData = new UserData(email, softwareFmId0, userKey0);
		IContainer localReadWriter = getLocalApi().makeContainer();
		MyDetails myDetails = new MyDetails(shell, localReadWriter, cardConfig, userData);
		checkProjectDetails(myDetails);
	}

	@Override
	protected IExtraReaderWriterConfigurator<LocalConfig> getLocalExtraReaderWriterConfigurator() {
		return new IExtraReaderWriterConfigurator<LocalConfig>() {
			@Override
			public void builder(IContainerBuilder builder, LocalConfig apiConfig) {
				builder.register(IProjectTimeGetter.class, new ProjectTimeGetterFixture());
				builder.register(IUsageReader.class, getProjectTimeGetterFixture(builder));
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void checkProjectDetails(MyDetails myDetails) {
		dispatchUntilJobsFinished();
		DataComposite<Table> composite = (DataComposite<Table>) myDetails.getComposite();
		Table projectTable = composite.getEditor();
		Swts.checkColumns(projectTable, "Group ID", "Artifact ID", "Jan 2012", "Feb 2012", "Mar 2012");

		assertEquals(7, projectTable.getItemCount());
		Swts.checkRow(projectTable, 0, "groupId0", "artifact11", "4", "4", "4");
		Swts.checkRow(projectTable, 1, "groupId0", "artifact12", "3", "0", "3");
		Swts.checkRow(projectTable, 2, "groupId0", "artifact13", "0", "3", "0");
		Swts.checkRow(projectTable, 3, "groupId1", "artifact21", "2", "2", "0");
		Swts.checkRow(projectTable, 4, "groupId1", "artifact22", "3", "3", "0");
		Swts.checkRow(projectTable, 5, "groupId2", "artifact31", "0", "0", "2");
		Swts.checkRow(projectTable, 6, "groupId2", "artifact32", "0", "0", "3");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = CardDataStoreFixture.syncCardConfig(display);
	}
}