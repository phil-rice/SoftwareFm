/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.mysoftwareFm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.LocalGroupsReader;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.IRequestGroupReportGeneration;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.eclipse.user.UserMembershipReaderForLocal;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.card.composites.CompositeWithCardMargin;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyPeople;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;

public class MyPeople implements IHasComposite {
	public static IShowMyPeople showMyPeople(final IServiceExecutor executor, final IMasterDetailSocial masterDetailSocial, final CardConfig cardConfig, final IGitLocal gitLocal, final IUrlGenerator userUrlGenerator, IUrlGenerator groupUrlGenerator, final IProjectTimeGetter timeGetter, final IRequestGroupReportGeneration reportGenerator, final long timeoutMs) {
		final IUserReader user = IUserReader.Utils.localUserReader(gitLocal, userUrlGenerator);
		final IGroupsReader groupsReader = new LocalGroupsReader(groupUrlGenerator, gitLocal);
		return new IShowMyPeople() {
			@Override
			public void showMyPeople(final UserData userData, final String groupId, final String artifactId) {
				executor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						final UserMembershipReaderForLocal membershipReader = new UserMembershipReaderForLocal(userUrlGenerator, gitLocal, user, userData.crypto);
						MyPeople myPeople = masterDetailSocial.createAndShowDetail(new IFunction1<Composite, MyPeople>() {
							@Override
							public MyPeople apply(Composite from) throws Exception {
								return new MyPeople(from, cardConfig, userData, membershipReader, groupsReader, timeGetter, reportGenerator, timeoutMs);
							}
						});
						myPeople.setData(groupId, artifactId);
						return null;
					}
				});
			}
		};
	}

	static class MyPeopleComposite extends CompositeWithCardMargin {

		private final StyledText text;
		private final Table table;

		public MyPeopleComposite(Composite parent, int style, final CardConfig cc, IProjectTimeGetter timeGetter) {
			super(parent, style, cc);
			text = new StyledText(this, SWT.WRAP | SWT.READ_ONLY);
			table = new Table(this, SWT.FULL_SELECTION);
			addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					Rectangle ca = getClientArea();
					e.gc.drawRoundRectangle(ca.x - cc.cornerRadiusComp, ca.y - cc.cornerRadiusComp, ca.width + 2 * cc.cornerRadiusComp, ca.height + 2 * cc.cornerRadiusComp, cc.cornerRadius, cc.cornerRadius);
				}
			});
			new TableColumn(table, SWT.NULL).setText("Person");
			for (String month : timeGetter.lastNMonths(3)) {
				LineItem lineItem = new LineItem(GroupConstants.myGroupsCardType, month, null);
				String name = cc.nameFn.apply(cc, lineItem);
				new TableColumn(table, SWT.NULL).setText(name);
			}
			new TableColumn(table, SWT.NULL).setText("Groups");
			table.setHeaderVisible(true);
			Swts.packTables(table);
		}

		public void setSoftwareFmIds(String groupId, String artifactId, Set<String> softwareFmIds, Map<String, String> softwareFmIdToName, Map<String, List<String>> softwareFmIdToGroups, Map<String, Map<String, List<Integer>>> softwareFmIdToMonthToUsage) {
			text.setText("Group Id: " + groupId + "\nArtifact Id: " + artifactId);
			List<String> ids = Lists.sort(softwareFmIds);
			for (String id : ids) {
				TableItem item = new TableItem(table, SWT.NULL);
				String name = softwareFmIdToName.get(id);
				String group = Strings.join(Lists.sort(softwareFmIdToGroups.get(id)), ", ");
				item.setText(0, name);
				int i = 1;
				for (Entry<String, List<Integer>> e : softwareFmIdToMonthToUsage.get(id).entrySet()) {
					item.setText(i++, Integer.toString(e.getValue().size()));
				}
				item.setText(4, group);
			}
			Swts.packColumns(table);
			layout();
		}
	}

	private final MyPeopleComposite content;
	private final UserData userData;
	private final IUserMembershipReader membershipReader;
	private final IGroupsReader groupsReader;
	private final IProjectTimeGetter timeGetter;
	private final IRequestGroupReportGeneration reportGenerator;
	private final long timeoutMs;

	public MyPeople(Composite parent, CardConfig cardConfig, UserData userData, IUserMembershipReader membershipReader, IGroupsReader groupsReader, IProjectTimeGetter timeGetter, IRequestGroupReportGeneration reportGenerator, long timeoutMs) {
		this.userData = userData;
		this.membershipReader = membershipReader;
		this.groupsReader = groupsReader;
		this.timeGetter = timeGetter;
		this.reportGenerator = reportGenerator;
		this.timeoutMs = timeoutMs;
		this.content = new MyPeopleComposite(parent, SWT.NULL, cardConfig, timeGetter);
		Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildrenWithLastGrabingVertical(content);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setData(final String groupId, final String artifactId) {
		final Set<String> softwareFmIds = Sets.newSet();
		final Map<String, String> softwareFmIdToName = Maps.newMap();
		final Map<String, List<String>> softwareFmIdToGroups = Maps.newMap();
		final Map<String, Map<String, List<Integer>>> softwareFmIdToMonthToUsage = Maps.newMap();

		List<Map<String, Object>> walkGroups = membershipReader.walkGroupsFor(userData.softwareFmId);
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
				generateReportIfPossible(groupId, groupsCrypto, month);
				Map<String, Map<String, Map<String, List<Integer>>>> report = (Map) groupsReader.getUsageReport(groupsId, groupsCrypto, month);
				if (report != null)
					for (Entry<String, Map<String, Map<String, List<Integer>>>> groupEntry : report.entrySet())
						if (groupId.equals(groupEntry.getKey()))
							for (Entry<String, Map<String, List<Integer>>> artifactEntry : groupEntry.getValue().entrySet())
								if (artifactId.equals(artifactEntry.getKey()))
									for (Entry<String, List<Integer>> e : artifactEntry.getValue().entrySet()) {
										String softwareFmId = e.getKey();
										softwareFmIds.add(softwareFmId);
										Maps.addToList(softwareFmIdToGroups, softwareFmId, groupName);
										Maps.addToMapOfMaps(softwareFmIdToMonthToUsage, HashMap.class, softwareFmId, month, e.getValue());
									}
			}
		}
		Swts.syncExec(content, new Runnable() {
			@Override
			public void run() {
				content.setSoftwareFmIds(groupId, artifactId, softwareFmIds, softwareFmIdToName, softwareFmIdToGroups, softwareFmIdToMonthToUsage);
			}
		});
	}

	protected void generateReportIfPossible(final String groupId, String groupsCrypto, String month) {
		try {
			reportGenerator.request(groupId, groupsCrypto, month).get(timeoutMs, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			// it wasn't possible
		}
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