/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.mysoftwareFm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.LocalGroupsReader;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.comparators.Comparators;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.IRequestGroupReportGeneration;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.eclipse.user.UserMembershipReaderForLocal;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.DataComposite;
import org.softwareFm.swt.editors.DataCompositeWithFooterLayout;
import org.softwareFm.swt.editors.IDataCompositeWithFooter;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;

public class MyGroups implements IHasComposite {
	public static IShowMyGroups showMyGroups(final IServiceExecutor executor, final CardConfig cardConfig, final IMasterDetailSocial masterDetailSocial, final IUrlGenerator userUrlGenerator, final IUrlGenerator groupUrlGenerator, final IGitLocal gitLocal, final IProjectTimeGetter projectTimeGetter, final IRequestGroupReportGeneration reportGenerator, final IGroupClientOperations groupClientOperations) {
		return new IShowMyGroups() {
			@Override
			public void show(final UserData userData) {
				executor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						final IUserReader user = IUserReader.Utils.localUserReader(gitLocal, userUrlGenerator);
//						String membershipCrypto = user.getUserProperty(userData.softwareFmId, userData.crypto, GroupConstants.membershipCryptoKey);
						final IGroupsReader groupsReader = new LocalGroupsReader(groupUrlGenerator, gitLocal);
						// if (membershipCrypto == null) {
						// masterDetailSocial.createAndShowDetail(new IFunction1<Composite, TextInCompositeWithCardMargin>() {
						// @Override
						// public TextInCompositeWithCardMargin apply(Composite from) throws Exception {
						// TextInCompositeWithCardMargin result = new TextInCompositeWithCardMargin(from, SWT.WRAP | SWT.READ_ONLY, cardConfig);
						// result.setText("You belong to no groups");
						// return result;
						// }
						// });
						// return null;
						// }
						final IUserMembershipReader userMembershipReader = new UserMembershipReaderForLocal(userUrlGenerator, gitLocal, user);
						Swts.asyncExec(masterDetailSocial.getControl(), new Runnable() {
							@Override
							public void run() {
								masterDetailSocial.hideSocial();
								masterDetailSocial.createAndShowDetail(new IFunction1<Composite, MyGroups>() {
									@Override
									public MyGroups apply(Composite from) throws Exception {
										return new MyGroups(from, cardConfig, userMembershipReader, groupsReader, userData, projectTimeGetter, reportGenerator, groupClientOperations);
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

	private final MyGroupsComposite content;

	public static class MyGroupsButtons implements IHasControl {
		private final Composite content;
		public final Button invite;
		public final Button create;
		public final Button accept;

		public MyGroupsButtons(Composite parent, IGroupClientOperations groupClientOperations, UserData userData) {
			this.content = new Composite(parent, SWT.NULL);
			content.setLayout(Swts.Row.getHorizonalNoMarginRowLayout());
			accept = Swts.Buttons.makePushButton(content, "Accept", groupClientOperations.acceptInvitation(userData));
			invite = Swts.Buttons.makePushButton(content, "Invite", groupClientOperations.inviteToGroup(userData));
			create = Swts.Buttons.makePushButton(content, "Create new group", groupClientOperations.createGroup(userData));
		}

		@Override
		public Control getControl() {
			return content;
		}
	}

	public static class MyGroupsComposite extends DataComposite<SashForm> implements IDataCompositeWithFooter<SashForm, MyGroupsButtons> {
		private final Table summaryTable;
		private final IGroupsReader groupsReader;
		private final SashForm sashForm;
		private final Composite rightHand;
		private final Table membershipTable;
		private final Map<String, String> idToCrypto = Maps.newMap();
		private final MyGroupsButtons buttons;

		@Override
		public MyGroupsButtons getFooter() {
			return buttons;
		}

		@Override
		public SashForm getEditor() {
			return sashForm;
		}

		public MyGroupsComposite(Composite parent, final CardConfig cardConfig, IUserMembershipReader membershipReader, final IGroupsReader groupReaders, UserData userData,  IProjectTimeGetter projectTimeGetter, final IRequestGroupReportGeneration reportGenerator, IGroupClientOperations groupClientOperations) {
			super(parent, cardConfig, GroupConstants.myGroupsCardType, SoftwareFmConstants.myGroupsTitle, true);
			this.groupsReader = groupReaders;
			sashForm = new SashForm(getInnerBody(), SWT.HORIZONTAL);
			summaryTable = new Table(sashForm, SWT.FULL_SELECTION);
			buttons = new MyGroupsButtons(getInnerBody(), groupClientOperations, userData);
			summaryTable.setHeaderVisible(true);
			new TableColumn(summaryTable, SWT.NULL).setText("Group Name");
			new TableColumn(summaryTable, SWT.NULL).setText("Members");
			new TableColumn(summaryTable, SWT.NULL).setText("My Status");
			List<Map<String, Object>> groups = membershipReader.walkGroupsFor(userData.softwareFmId, userData.crypto);
			for (Map<String, Object> map : groups) {
				String groupId = (String) map.get(GroupConstants.groupIdKey);
				String groupCryptoKey = (String) map.get(GroupConstants.groupCryptoKey);
				String groupName = groupReaders.getGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey);
				TableItem item = new TableItem(summaryTable, SWT.NULL);
				item.setData(groupId);
				int membershipCount = groupsReader.membershipCount(groupId, groupCryptoKey);
				String membershipCountString = Integer.toString(membershipCount);
				String myStatus = Strings.nullSafeToString( map.get(GroupConstants.membershipStatusKey));
				item.setText(new String[] { groupName, membershipCountString , myStatus});
				idToCrypto.put(groupId, groupCryptoKey);
			}

			rightHand = new Composite(sashForm, SWT.NULL);
			final StackLayout stackLayout = new StackLayout();
			rightHand.setLayout(stackLayout);

			StyledText textInBorder = new StyledText(rightHand, SWT.WRAP | SWT.READ_ONLY);
			textInBorder.setText(IResourceGetter.Utils.getOrException(getResourceGetter(), GroupConstants.needToSelectGroup));
			stackLayout.topControl = textInBorder;

			membershipTable = new Table(rightHand, SWT.FULL_SELECTION);
			membershipTable.setHeaderVisible(true);
			new TableColumn(membershipTable, SWT.NULL).setText("Email");
			new TableColumn(membershipTable, SWT.NULL).setText("Status");
			summaryTable.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					stackLayout.topControl = membershipTable;
					membershipTable.removeAll();
					int index = summaryTable.getSelectionIndex();
					if (index >= 0) {
						TableItem item = summaryTable.getItem(index);
						String groupId = (String) item.getData();
						if (groupId == null)
							throw new NullPointerException(Integer.toString(index));
						String groupCryptoKey = idToCrypto.get(groupId);
						if (groupCryptoKey == null)
							throw new NullPointerException(groupCryptoKey);
						for (Map<String, Object> user : Lists.sort( groupsReader.users(groupId, groupCryptoKey), Comparators.mapKey(LoginConstants.emailKey))){
							new TableItem(membershipTable, SWT.NULL).setText(new String[]{ Strings.nullSafeToString(user.get(LoginConstants.emailKey)), Strings.nullSafeToString(user.get(GroupConstants.membershipStatusKey))});
						}
							
					}
					Swts.packColumns(membershipTable);
					rightHand.layout();
				}
			});
			Swts.packTables(summaryTable, membershipTable);
			sashForm.setWeights(new int[] { 2, 3 });
		}

	}

	public MyGroups(Composite parent, CardConfig cardConfig, IUserMembershipReader membershipReader, IGroupsReader groupsReader, UserData userData, IProjectTimeGetter projectTimeGetter, IRequestGroupReportGeneration reportGenerator,IGroupClientOperations groupClientOperations) {
		content = new MyGroupsComposite(parent, cardConfig, membershipReader, groupsReader, userData, projectTimeGetter, reportGenerator, groupClientOperations);
		content.setLayout(new DataCompositeWithFooterLayout());
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