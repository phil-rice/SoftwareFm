/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.mysoftwareFm;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.comparators.Comparators;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.CommonMessages;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.monitor.IMonitor;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.IRequestGroupReportGeneration;
import org.softwareFm.eclipse.constants.EclipseMessages;
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
	public static IShowMyGroups showMyGroups(final IServiceExecutor executor, final boolean showDialogs, final CardConfig cardConfig, final IMasterDetailSocial masterDetailSocial, final IUrlGenerator userUrlGenerator, final IUrlGenerator groupUrlGenerator, final IGitLocal gitLocal, final IProjectTimeGetter projectTimeGetter, final IRequestGroupReportGeneration reportGenerator, final IGroupClientOperations groupClientOperations) {
		return new IShowMyGroups() {
			@Override
			public void show(final UserData userData, final String groupId) {
				executor.submit(new IFunction1<IMonitor, Void>() {
					@Override
					public Void apply(IMonitor monitor) throws Exception {
						monitor.beginTask(EclipseMessages.showMyGroups, 2);
						final IUserReader user = IUserReader.Utils.localUserReader(gitLocal, userUrlGenerator);
						// String membershipCrypto = user.getUserProperty(userData.softwareFmId, userData.crypto, GroupConstants.membershipCryptoKey);
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
						gitLocal.clearCaches();
						Swts.asyncExecAndMarkDone(masterDetailSocial.getControl(), monitor, new Runnable() {
							@Override
							public void run() {
								masterDetailSocial.hideSocial();
								masterDetailSocial.createAndShowDetail(new IFunction1<Composite, MyGroups>() {
									@Override
									public MyGroups apply(Composite from) throws Exception {
										MyGroups myGroups = new MyGroups(from, showDialogs, cardConfig, userMembershipReader, groupsReader, userData, projectTimeGetter, reportGenerator, groupClientOperations, new ICallback<String>() {
											@Override
											public void process(String groupId) throws Exception {
												showMyGroups(executor, showDialogs, cardConfig, masterDetailSocial, userUrlGenerator, groupUrlGenerator, gitLocal, projectTimeGetter, reportGenerator, groupClientOperations).show(userData, groupId);
											}
										});
										myGroups.selectAndScrollTo(groupId);
										return myGroups;
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
		public final Button kick;
		public Button leave;
		private final Callable<IdNameAndStatus> idNameStatusGetter;
		private final Callable<List<Map<String, Object>>> objectMapGetter;
		private final Callable<Integer> groupSizeGetter;

		@SuppressWarnings("Need to externalise these string")
		//show dialogs exists because it is very hard to test for this: the dialog actually appears... I could rewrite the open confirm dialog so that it didn't appear in tests, but it doesn't seem worth it
		public MyGroupsButtons(final Composite parent, final boolean showDialogs, final IGroupClientOperations groupClientOperations, final UserData userData, final ICallback<String> showMyGroups, final Callable<IdNameAndStatus> idNameStatusGetter, final Callable<List<Map<String, Object>>> objectMapGetter, Callable<Integer> groupSizeGetter) {
			this.idNameStatusGetter = idNameStatusGetter;
			this.objectMapGetter = objectMapGetter;
			this.groupSizeGetter = groupSizeGetter;
			this.content = new Composite(parent, SWT.NULL);
			content.setLayout(Swts.Row.getHorizonalNoMarginRowLayout());
			accept = Swts.Buttons.makePushButton(content, "Accept", groupClientOperations.acceptInvitation(userData, idNameStatusGetter, showMyGroups));
			invite = Swts.Buttons.makePushButton(content, "Invite", groupClientOperations.inviteToGroup(userData, idNameStatusGetter, showMyGroups));
			create = Swts.Buttons.makePushButton(content, "Create new group", groupClientOperations.createGroup(userData, showMyGroups));
			Runnable kickRunnable = new Runnable() {
				@Override
				public void run() {
					if (showDialogs && !MessageDialog.openConfirm(parent.getShell(), "Kick", MessageFormat.format("Are you sure you want to kick {0} members", Callables.call(objectMapGetter).size())))
						return;
					groupClientOperations.kickMember(userData, idNameStatusGetter, objectMapGetter, new ICallback<String>() {
						@Override
						public void process(String t) throws Exception {
							showMyGroups.process(t);
						}
					}).run();
				}
			};
			leave = Swts.Buttons.makePushButton(content, "Leave", new Runnable() {
				@Override
				public void run() {
					if (showDialogs && !MessageDialog.openConfirm(parent.getShell(), "Leave", MessageFormat.format("Are you sure you want to leave", Callables.call(objectMapGetter).size())))
						return;
					groupClientOperations.leaveGroup(userData, showMyGroups, idNameStatusGetter).run();					
				}
			});
			
			kick = Swts.Buttons.makePushButton(content, "Kick", kickRunnable);
		}

		@Override
		public Control getControl() {
			return content;
		}

		public void sortOutButtonStatus() {
			IdNameAndStatus idNameAndStatus = Callables.call(idNameStatusGetter);
			List<Map<String, Object>> objectUsers = Callables.call(objectMapGetter);
			int groupSize = Callables.call(groupSizeGetter);
			boolean invited = idNameAndStatus != null && GroupConstants.invitedStatus.equals(idNameAndStatus.status);
			boolean admin = idNameAndStatus != null && GroupConstants.adminStatus.equals(idNameAndStatus.status);
			boolean someOneSelected = objectUsers.size() > 0;
			boolean someSelectedIsAdmin = someOneSelected && areAnyAdmin(objectUsers);
			boolean kickstatus = admin && someOneSelected && !someSelectedIsAdmin;
			boolean lastMember = groupSize ==1;
			boolean leaveStatus = idNameAndStatus != null && (!admin || lastMember); 
			accept.setEnabled(invited);
			invite.setEnabled(admin);
			kick.setEnabled(kickstatus);
			leave.setEnabled(leaveStatus);
			
		}

		private boolean areAnyAdmin(List<Map<String, Object>> objectUsers) {
			for (Map<String, Object> user : objectUsers)
				if (GroupConstants.adminStatus.equals(user.get(GroupConstants.membershipStatusKey)))
					return true;
			return false;
		}
	}

	public static class MyGroupsComposite extends DataComposite<SashForm> implements IDataCompositeWithFooter<SashForm, MyGroupsButtons> {
		public final Table summaryTable;
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

		public MyGroupsComposite(Composite parent, boolean showDialogs, final CardConfig cardConfig, IUserMembershipReader membershipReader, final IGroupsReader groupReaders, UserData userData, IProjectTimeGetter projectTimeGetter, final IRequestGroupReportGeneration reportGenerator, IGroupClientOperations groupClientOperations, ICallback<String> showMyGroups) {
			super(parent, cardConfig, GroupConstants.myGroupsCardType, SoftwareFmConstants.myGroupsTitle, true);
			this.groupsReader = groupReaders;
			sashForm = new SashForm(getInnerBody(), SWT.HORIZONTAL);
			summaryTable = new Table(sashForm, SWT.FULL_SELECTION);
			buttons = new MyGroupsButtons(getInnerBody(), showDialogs, groupClientOperations, userData, showMyGroups, new Callable<IdNameAndStatus>() {
				@Override
				public IdNameAndStatus call() throws Exception {
					int index = summaryTable.getSelectionIndex();
					if (index == -1)
						return null;
					Object result = summaryTable.getItem(index).getData();
					return (IdNameAndStatus) result;

				}
			}, new Callable<List<Map<String, Object>>>() {
				@SuppressWarnings("unchecked")
				@Override
				public List<Map<String, Object>> call() throws Exception {
					List<Map<String, Object>> result = Lists.newList();
					for (int i : membershipTable.getSelectionIndices())
						result.add((Map<String, Object>) membershipTable.getItem(i).getData());
					return result;
				}
			},new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					return membershipTable.getItemCount();
				}
			});
			summaryTable.setHeaderVisible(true);
			new TableColumn(summaryTable, SWT.NULL).setText("Group Name");
			new TableColumn(summaryTable, SWT.NULL).setText("Members");
			new TableColumn(summaryTable, SWT.NULL).setText("My Status");
			Iterable<Map<String, Object>> groups = membershipReader.walkGroupsFor(userData.softwareFmId, userData.crypto);
			List<Map<String, Object>> groupsWithName = Lists.map(groups, new IFunction1<Map<String, Object>, Map<String, Object>>() {
				@Override
				public Map<String, Object> apply(Map<String, Object> map) throws Exception {
					if (map.containsKey(CommonConstants.errorKey))
						return map;
					String groupId = (String) map.get(GroupConstants.groupIdKey);
					String groupCryptoKey = (String) map.get(GroupConstants.groupCryptoKey);
					try {
						String groupName = groupReaders.getGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey);
						return Maps.with(map, GroupConstants.groupNameKey, groupName);
					} catch (Exception e) {
						return Maps.with(map, CommonConstants.errorKey, MessageFormat.format(GroupConstants.cannotDetermineGroupName, groupId, e));
					}
				}
			});

			for (Map<String, Object> map : Lists.sort(groupsWithName, Comparators.mapKey(GroupConstants.groupNameKey))) {
				if (map.containsKey(CommonConstants.errorKey)) {
					TableItem item = new TableItem(summaryTable, SWT.NULL);
					item.setText(new String[] { CommonMessages.corrupted, CommonMessages.record, "" });
				} else {
					String groupId = (String) map.get(GroupConstants.groupIdKey);
					String groupCryptoKey = (String) map.get(GroupConstants.groupCryptoKey);
					String groupName = groupReaders.getGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey);
					TableItem item = new TableItem(summaryTable, SWT.NULL);
					int membershipCount = groupsReader.membershipCount(groupId, groupCryptoKey);
					String membershipCountString = Integer.toString(membershipCount);
					String myStatus = Strings.nullSafeToString(map.get(GroupConstants.membershipStatusKey));
					IdNameAndStatus data = new IdNameAndStatus(groupId, groupName, myStatus);
					item.setData(data);
					item.setText(new String[] { groupName, membershipCountString, myStatus });
					idToCrypto.put(groupId, groupCryptoKey);
				}
			}

			rightHand = new Composite(sashForm, SWT.NULL);
			final StackLayout stackLayout = new StackLayout();
			rightHand.setLayout(stackLayout);

			StyledText textInBorder = new StyledText(rightHand, SWT.WRAP | SWT.READ_ONLY);
			textInBorder.setText(IResourceGetter.Utils.getOrException(getResourceGetter(), GroupConstants.needToSelectGroup));
			stackLayout.topControl = textInBorder;

			membershipTable = new Table(rightHand, SWT.FULL_SELECTION | SWT.MULTI);
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
						IdNameAndStatus idAndName = (IdNameAndStatus) item.getData();
						if (idAndName == null)
							return;
						String groupId = idAndName.id;
						if (groupId == null)
							throw new NullPointerException("GroupId is null: " + Integer.toString(index));
						String groupCryptoKey = idToCrypto.get(groupId);
						if (groupCryptoKey == null)
							throw new NullPointerException("GroupCrypto is null: " + Integer.toString(index));
						for (Map<String, Object> user : Lists.sort(groupsReader.users(groupId, groupCryptoKey), Comparators.mapKey(LoginConstants.emailKey))) {
							TableItem tableItem = new TableItem(membershipTable, SWT.NULL);
							if (user.containsKey(CommonConstants.errorKey))
								tableItem.setText(new String[] { CommonMessages.corrupted, CommonMessages.record });
							else {
								tableItem.setData(user);
								tableItem.setText(new String[] { Strings.nullSafeToString(user.get(LoginConstants.emailKey)), Strings.nullSafeToString(user.get(GroupConstants.membershipStatusKey)) });
							}
						}

					}
					Swts.packColumns(membershipTable);
					rightHand.layout();
					sortOutButtonsEnabledStatus();
				}
			});
			membershipTable.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					sortOutButtonsEnabledStatus();
				}
			});
			Swts.packTables(summaryTable, membershipTable);
			sashForm.setWeights(new int[] { 2, 3 });
			sortOutButtonsEnabledStatus();
		}

		private void sortOutButtonsEnabledStatus() {
			buttons.sortOutButtonStatus();
		}
	}

	public MyGroups(Composite parent, boolean showDialogs, CardConfig cardConfig, IUserMembershipReader membershipReader, IGroupsReader groupsReader, UserData userData, IProjectTimeGetter projectTimeGetter, IRequestGroupReportGeneration reportGenerator, IGroupClientOperations groupClientOperations, ICallback<String> showMyGroups) {
		content = new MyGroupsComposite(parent, showDialogs, cardConfig, membershipReader, groupsReader, userData, projectTimeGetter, reportGenerator, groupClientOperations, showMyGroups);
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

	protected void selectAndScrollTo(String groupId) {
		Table table = content.summaryTable;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			IdNameAndStatus idNameAndStatus = (IdNameAndStatus) item.getData();
			if (idNameAndStatus != null && idNameAndStatus.id.equals(groupId)) {
				Swts.selectOnlyAndNotifyListener(table, i);
				table.showItem(item);
			}
		}

	}

}