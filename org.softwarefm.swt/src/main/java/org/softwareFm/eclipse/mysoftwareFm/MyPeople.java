/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.mysoftwareFm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.jarAndClassPath.api.IProjectTimeGetter;
import org.softwareFm.jarAndClassPath.api.IRequestGroupReportGeneration;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.ISwtFunction1;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.DataComposite;
import org.softwareFm.swt.editors.DataCompositeLayout;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyPeople;
import org.softwareFm.swt.swt.Swts;

public class MyPeople implements IHasComposite {
	public static IShowMyPeople showMyPeople(final IContainer container, final IMasterDetailSocial masterDetailSocial, final CardConfig cardConfig, final long timeoutMs) {
		return new IShowMyPeople() {
			@Override
			public void showMyPeople(final UserData userData, final String groupId, final String artifactId) {
				container.accessWithCallbackFn(IGitReader.class, Functions.<IGitReader, Void> constant(null), new ISwtFunction1<Void, Void>() {
					@Override
					public Void apply(Void from) throws Exception {
						MyPeople myPeople = masterDetailSocial.createAndShowDetail(new IFunction1<Composite, MyPeople>() {
							@Override
							public MyPeople apply(Composite from) throws Exception {
								return new MyPeople(from, container, cardConfig, userData);
							}
						});
						myPeople.setData(groupId, artifactId);
						return null;
					}
				});
			}
		};
	}

	static class MyPeopleComposite extends DataComposite<Table> {

		private final Table table;
		private final Iterable<String> months;

		@Override
		public Table getEditor() {
			return table;
		}

		// TODO Evil threadiness
		public MyPeopleComposite(Composite parent, IContainer container, int style, final CardConfig cc) {
			super(parent, cc, CardConstants.loginCardType, JarAndPathConstants.peopleIKnowLoadingTitle, true);
			table = new Table(getInnerBody(), SWT.FULL_SELECTION);
			addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					Rectangle ca = getClientArea();
					e.gc.drawRoundRectangle(ca.x - cc.cornerRadiusComp, ca.y - cc.cornerRadiusComp, ca.width + 2 * cc.cornerRadiusComp, ca.height + 2 * cc.cornerRadiusComp, cc.cornerRadius, cc.cornerRadius);
				}
			});
			new TableColumn(table, SWT.NULL).setText("Person");
			months = container.access(IProjectTimeGetter.class, Functions.<IProjectTimeGetter, IProjectTimeGetter> identity()).get().lastNMonths(3);
			for (String month : months) {
				String name = MySoftwareFmFunctions.monthFileNameToPrettyName(month);
				new TableColumn(table, SWT.NULL).setText(name);
			}
			new TableColumn(table, SWT.NULL).setText("Groups");
			table.setHeaderVisible(true);
			Swts.packTables(table);
		}

		public void setSoftwareFmIds(String groupId, String artifactId, Set<String> softwareFmIds, Map<String, String> softwareFmIdToName, Map<String, Set<String>> softwareFmIdToGroups, Map<String, Map<String, List<Integer>>> softwareFmIdToMonthToUsage) {
			String title = IResourceGetter.Utils.getMessageOrException(getCardConfig().resourceGetterFn, getCardType(), JarAndPathConstants.peopleIKnowTitle, groupId, artifactId);
			setTitleAndImage(title, "", CardConstants.loginCardType);
			List<String> ids = Lists.sort(softwareFmIds);
			for (String id : ids) {
				TableItem item = new TableItem(table, SWT.NULL);
				String name = softwareFmIdToName.get(id);
				String group = Strings.join(Lists.sort(softwareFmIdToGroups.get(id)), ", ");
				item.setText(0, name);
				Map<String, List<Integer>> monthToUsage = softwareFmIdToMonthToUsage.get(id);
				int i = 0;
				if (monthToUsage != null) {
					for (String month : months) {
						i++;
						List<Integer> days = monthToUsage.get(month);
						if (days != null)
							item.setText(i, Integer.toString(days.size()));
					}
				}
				item.setText(4, group);
			}
			Swts.packColumns(table);
			layout();
		}
	}

	private final MyPeopleComposite content;
	private final UserData userData;
	private final IContainer container;

	public MyPeople(Composite parent, IContainer readWriteApi, CardConfig cardConfig, UserData userData) {
		this.container = readWriteApi;
		this.userData = userData;
		this.content = new MyPeopleComposite(parent, readWriteApi, SWT.NULL, cardConfig);
		content.setLayout(new DataCompositeLayout());

	}

	@SuppressWarnings({ "unchecked", "rawtypes" , "provide composite object for result"})
	public void setData(final String groupId, final String artifactId) {
		final Set<String> softwareFmIds = Sets.newSet();
		final Map<String, String> softwareFmIdToName = Maps.newMap();
		final Map<String, Set<String>> softwareFmIdToGroups = Maps.newMap();
		final Map<String, Map<String, List<Integer>>> softwareFmIdToMonthToUsage = Maps.newMap();

		container.accessWithCallbackFn(IUserMembershipReader.class, IProjectTimeGetter.class, IGroupsReader.class, new IFunction3<IUserMembershipReader, IProjectTimeGetter, IGroupsReader, Void>() {
			@Override
			public Void apply(IUserMembershipReader membershipReader, IProjectTimeGetter timeGetter, IGroupsReader groupsReader) throws Exception {
				Iterable<Map<String, Object>> walkGroups = membershipReader.walkGroupsFor(userData.softwareFmId, userData.crypto);

				for (Map<String, Object> groupData : walkGroups)
					for (String month : timeGetter.lastNMonths(3)) {
						String groupsId = (String) groupData.get(GroupConstants.groupIdKey);
						String groupsCrypto = (String) groupData.get(GroupConstants.groupCryptoKey);
						if (groupsId == null)
							throw new NullPointerException(groupsId);
						if (groupsCrypto == null)
							throw new NullPointerException(groupsCrypto);
						generateReportIfPossible(groupsId, groupsCrypto, month);
					}

				for (Map<String, Object> groupData : walkGroups) {
					String groupsId = (String) groupData.get(GroupConstants.groupIdKey);
					String groupsCrypto = (String) groupData.get(GroupConstants.groupCryptoKey);
					if (groupsId == null)
						throw new NullPointerException(groupsId);
					if (groupsCrypto == null)
						throw new NullPointerException(groupsCrypto);
					String groupName = groupsReader.getGroupProperty(groupsId, groupsCrypto, GroupConstants.groupNameKey);
					Iterable<Map<String, Object>> users = groupsReader.users(groupsId, groupsCrypto);
					for (Map<String, Object> userData : users) {
						String softwareFmId = (String) userData.get(LoginConstants.softwareFmIdKey);
						String email = (String) userData.get(LoginConstants.emailKey);
						softwareFmIdToName.put(softwareFmId, email);
					}

					for (String month : timeGetter.lastNMonths(3)) {
						generateReportIfPossible(groupsId, groupsCrypto, month);
						Map<String, Map<String, Map<String, List<Integer>>>> report = (Map) groupsReader.getUsageReport(groupsId, groupsCrypto, month);
						System.out.println("Id: " + groupsId + " Month: " + month + " Report: " + report);
						if (report != null) {
							for (Entry<String, Map<String, Map<String, List<Integer>>>> groupEntry : report.entrySet())
								if (groupId.equals(groupEntry.getKey()))
									for (Entry<String, Map<String, List<Integer>>> artifactEntry : groupEntry.getValue().entrySet())
										if (artifactId.equals(artifactEntry.getKey()))
											for (Entry<String, List<Integer>> e : artifactEntry.getValue().entrySet()) {
												String softwareFmId = e.getKey();
												softwareFmIds.add(softwareFmId);
												Maps.addToCollection(softwareFmIdToGroups, HashSet.class, softwareFmId, groupName);
												Maps.addToMapOfMaps(softwareFmIdToMonthToUsage, HashMap.class, softwareFmId, month, e.getValue());
											}
						}
					}
				}
				return null;
			}
		}, new ISwtFunction1<Void, Void>() {
			@Override
			public Void apply(Void from) throws Exception {
				System.out.println("Report: ");
				System.out.println(softwareFmIdToMonthToUsage);
				content.setSoftwareFmIds(groupId, artifactId, softwareFmIds, softwareFmIdToName, softwareFmIdToGroups, softwareFmIdToMonthToUsage);
				return null;
			}
		});
	}

	protected void generateReportIfPossible(final String groupId, final String groupsCrypto, final String month) {
		container.access(IRequestGroupReportGeneration.class, new ICallback<IRequestGroupReportGeneration>() {
			@Override
			public void process(IRequestGroupReportGeneration reportGenerator) throws Exception {
				reportGenerator.request(groupId, groupsCrypto, month).get();
			}
		});
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