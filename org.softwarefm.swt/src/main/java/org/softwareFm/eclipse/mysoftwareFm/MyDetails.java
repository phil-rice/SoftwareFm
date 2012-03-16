/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.mysoftwareFm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.monitor.IMonitor;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.constants.EclipseMessages;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.project.UserAndProjectFactory;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.eclipse.user.IUsageReader;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.DataComposite;
import org.softwareFm.swt.editors.DataCompositeLayout;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;

public class MyDetails implements IHasComposite {

	public static IShowMyData showMyDetails(final IServiceExecutor executor, final CardConfig cardConfig, final IMasterDetailSocial masterDetailSocial, final IUrlGenerator userUrlGenerator, final IGitLocal gitLocal, final IProjectTimeGetter timeGetter) {
		return new IShowMyData() {
			@Override
			public void show(final UserData userData) {
				
				executor.submit(new IFunction1<IMonitor,Void>() {
					@Override
					public Void apply(IMonitor monitor) throws Exception {
						monitor.beginTask(EclipseMessages.showMyData, 2);
						final IUserReader user = IUserReader.Utils.localUserReader(gitLocal, userUrlGenerator);
						final IUsageReader project = UserAndProjectFactory.projectForLocal(user, userUrlGenerator, userData.crypto, gitLocal);
						
						Swts.asyncExec(masterDetailSocial.getControl(), new Runnable() {
							@Override
							public void run() {
								masterDetailSocial.hideSocial();
								masterDetailSocial.createAndShowDetail(new IFunction1<Composite, MyDetails>() {
									@Override
									public MyDetails apply(Composite from) throws Exception {
										return new MyDetails(from, cardConfig, userData, user, project, timeGetter);
									}
								});
							}
						});
						return null;
					}
				});
			}
		};
	}

	public static List<String> displayProperties = Arrays.asList(LoginConstants.emailKey, LoginConstants.monikerKey);

	static class MyProjectComposite extends DataComposite<Table> {

		private final Table projectDetails;

		@Override
		public Table getEditor() {
			return projectDetails;
		}

		public MyProjectComposite(Composite parent, int style, final CardConfig cc, UserData userData, IUserReader user, IUsageReader project, IProjectTimeGetter timeGetter) {
			super(parent, cc, CardConstants.loginCardType, SoftwareFmConstants.myProjectsTitle, true);

			Map<String, Map<String, Map<String, Integer>>> groupToArtifactToMonthToCount = Maps.newMap();
			Iterable<String> lastNMonths = timeGetter.lastNMonths(3);
			this.projectDetails = new Table(getInnerBody(), SWT.FULL_SELECTION);
			projectDetails.setHeaderVisible(true);
			new TableColumn(projectDetails, SWT.NULL).setText("Group ID");
			new TableColumn(projectDetails, SWT.NULL).setText("Artifact ID");
			for (String month : lastNMonths) {
				TableColumn column = new TableColumn(projectDetails, SWT.NULL);
				column.setText(MySoftwareFmFunctions.monthFileNameToPrettyName(month));
			}
			String projectCryptoKey = user.getUserProperty(userData.softwareFmId, userData.crypto, SoftwareFmConstants.projectCryptoKey);

			for (String month : lastNMonths) {
				Map<String, Map<String, List<Integer>>> monthDetails = project.getProjectDetails(userData.softwareFmId, projectCryptoKey, month);
				for (Entry<String, Map<String, List<Integer>>> groupEntry : monthDetails.entrySet())
					for (Entry<String, List<Integer>> artifactEntry : groupEntry.getValue().entrySet())
						Maps.addToMapOfMapOfMaps(groupToArtifactToMonthToCount, HashMap.class, groupEntry.getKey(), artifactEntry.getKey(), month, artifactEntry.getValue().size());
			}

			for (String groupId : Lists.sort(groupToArtifactToMonthToCount.keySet())) {
				Map<String, Map<String, Integer>> groupMap = groupToArtifactToMonthToCount.get(groupId);
				for (String artifactId : Lists.sort(groupMap.keySet())) {
					Map<String, Integer> artifactMap = groupMap.get(artifactId);
					TableItem item = new TableItem(projectDetails, SWT.FULL_SELECTION);
					item.setText(0, groupId);
					item.setText(1, artifactId);
					int index = 2;
					for (String month : lastNMonths) {
						Integer count = Maps.intFor(artifactMap, month);
						item.setText(index++, Integer.toString(count));
					}
				}
			}
			Swts.packTables(projectDetails);
		}
	}

	private final MyProjectComposite content;

	public MyDetails(Composite parent, CardConfig cardConfig, UserData userData, IUserReader user, IUsageReader project, IProjectTimeGetter timeGetter) {
		this.content = new MyProjectComposite(parent, SWT.NULL, cardConfig, userData, user, project, timeGetter);
		content.setLayout(new DataCompositeLayout());
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

}