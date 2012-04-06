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
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.jarAndClassPath.api.IProjectTimeGetter;
import org.softwareFm.jarAndClassPath.api.IUsageReader;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.ISwtFunction1;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.DataComposite;
import org.softwareFm.swt.editors.DataCompositeLayout;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.swt.Swts;

public class MyDetails implements IHasComposite {

	public static IShowMyData showMyDetails(final IContainer container, final CardConfig cardConfig, final IMasterDetailSocial masterDetailSocial) {
		return new IShowMyData() {
			@Override
			public void show(final UserData userData) {
				container.accessWithCallbackFn(IGitReader.class, Functions.<IGitReader, Void>constant(null), new ISwtFunction1<Void, Void>() {
					@Override
					public Void apply(Void from) throws Exception {
						masterDetailSocial.hideSocial();
						masterDetailSocial.createAndShowDetail(new IFunction1<Composite, MyDetails>() {
							@Override
							public MyDetails apply(Composite from) throws Exception {
								return new MyDetails(from, container, cardConfig, userData);
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

		public MyProjectComposite(Composite parent, int style, final CardConfig cc, final UserData userData, IContainer readWriteApi) {
			super(parent, cc, CardConstants.loginCardType, JarAndPathConstants.myProjectsTitle, true);
			this.projectDetails = new Table(getInnerBody(), SWT.FULL_SELECTION);
			final Iterable<String> lastNMonths = readWriteApi.access(IProjectTimeGetter.class, new IFunction1<IProjectTimeGetter, Iterable<String>>() {
				@Override
				public Iterable<String> apply(IProjectTimeGetter from) throws Exception {
					return from.lastNMonths(3);
				}
			}).get();
			readWriteApi.accessWithCallbackFn(IUserReader.class, IUsageReader.class, new IFunction2<IUserReader, IUsageReader, Map<String, Map<String, Map<String, Integer>>>>() {
				@Override
				public Map<String, Map<String, Map<String, Integer>>> apply(IUserReader user, IUsageReader project) throws Exception {
					Map<String, Map<String, Map<String, Integer>>> groupToArtifactToMonthToCount = Maps.newMap();
					String projectCryptoKey = user.getUserProperty(userData.softwareFmId, userData.crypto, JarAndPathConstants.projectCryptoKey);

					for (String month : lastNMonths) {
						Map<String, Map<String, List<Integer>>> monthDetails = project.getProjectDetails(userData.softwareFmId, projectCryptoKey, month);
						for (Entry<String, Map<String, List<Integer>>> groupEntry : monthDetails.entrySet())
							for (Entry<String, List<Integer>> artifactEntry : groupEntry.getValue().entrySet())
								Maps.addToMapOfMapOfMaps(groupToArtifactToMonthToCount, HashMap.class, groupEntry.getKey(), artifactEntry.getKey(), month, artifactEntry.getValue().size());
					}
					return groupToArtifactToMonthToCount;
				}
			}, new ISwtFunction1<Map<String, Map<String, Map<String, Integer>>>, Void>() {
				@Override
				public Void apply(Map<String, Map<String, Map<String, Integer>>> groupToArtifactToMonthToCount) throws Exception {
					projectDetails.setHeaderVisible(true);
					new TableColumn(projectDetails, SWT.NULL).setText("Group ID");
					new TableColumn(projectDetails, SWT.NULL).setText("Artifact ID");
					for (String month : lastNMonths) {
						TableColumn column = new TableColumn(projectDetails, SWT.NULL);
						column.setText(MySoftwareFmFunctions.monthFileNameToPrettyName(month));
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
					return null;
				}
			});
		}
	}

	private final MyProjectComposite content;

	public MyDetails(Composite parent, IContainer readWriteApi, CardConfig cardConfig, UserData userData) {
		this.content = new MyProjectComposite(parent, SWT.NULL, cardConfig, userData, readWriteApi);
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