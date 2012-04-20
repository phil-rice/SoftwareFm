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
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.comparators.Comparators;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.CommonMessages;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.ISwtFunction1;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.composites.IScrollableToId;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.DataComposite;
import org.softwareFm.swt.editors.DataCompositeWithFooterLayout;
import org.softwareFm.swt.editors.IDataCompositeWithFooter;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.swt.Swts;

public class MyGroups implements IHasComposite {
	final static String membershipCountKey = "membershipCount";

	public static IShowMyGroups showMyGroups(final IMasterDetailSocial masterDetailSocial, final IUserAndGroupsContainer container, final boolean showDialogs, final CardConfig cardConfig) {
		return new IShowMyGroups() {
			@Override
			public void show(final UserData userData, final String groupId) {
				IGitReader.Utils.clearCache(container);
				masterDetailSocial.hideSocial();
				masterDetailSocial.createAndShowDetail(new IFunction1<Composite, MyGroups>() {
					@Override
					public MyGroups apply(Composite from) throws Exception {
						final MyGroups myGroups = new MyGroups(from, masterDetailSocial, container, showDialogs, cardConfig, userData, new ICallback<String>() {

							@Override
							public void process(String groupId) throws Exception {
								IShowMyGroups showMyGroups = showMyGroups(masterDetailSocial, container, showDialogs, cardConfig);
								showMyGroups.show(userData, groupId);
							}
						}, new ICallback<IScrollableToId>() {
							@Override
							public void process(IScrollableToId t) throws Exception {
								t.selectAndScrollTo(groupId);
							}
						});
						return myGroups;
					}
				});
			}
		};
	}

	private final MyGroupsComposite content;

	public static class MyGroupsButtons implements IHasControl {
		private final Composite content;
		public Button invite;
		public Button create;
		public Button accept;
		public Button kick;
		public Button leave;
		private final Callable<IdNameAndStatus> idNameStatusGetter;
		private final Callable<List<Map<String, Object>>> objectMapGetter;
		private final Callable<Integer> groupSizeGetter;

		@SuppressWarnings("Need to externalise these string")
		// show dialogs exists because it is very hard to test for this: the dialog actually appears... I could rewrite the open confirm dialog so that it didn't appear in tests, but it doesn't seem worth it
		public MyGroupsButtons(final Composite parent, IMasterDetailSocial masterDetailSocial, final CardConfig cardConfig, IContainer readWriteApi, final boolean showDialogs, final UserData userData, final ICallback<String> showMyGroups, final Callable<IdNameAndStatus> idNameStatusGetter, final Callable<List<Map<String, Object>>> objectMapGetter, Callable<Integer> groupSizeGetter) {
			this.idNameStatusGetter = idNameStatusGetter;
			this.objectMapGetter = objectMapGetter;
			this.groupSizeGetter = groupSizeGetter;
			this.content = new Composite(parent, SWT.NULL);
			content.setLayout(Swts.Row.getHorizonalNoMarginRowLayout());
			final IGroupClientOperations groupClientOperations = IGroupClientOperations.Utils.groupOperations(masterDetailSocial, cardConfig, readWriteApi);
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
						public void process(String groupId) throws Exception {
							showMyGroups.process(groupId);
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
			boolean lastMember = groupSize == 1;
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

	public static class MyGroupsComposite extends DataComposite<SashForm> implements IDataCompositeWithFooter<SashForm, MyGroupsButtons>, IScrollableToId {
		public final Table summaryTable;
		private final SashForm sashForm;
		private final Composite rightHand;
		private final Table membershipTable;
		private final Map<String, String> idToCrypto = Maps.newMap();
		private final MyGroupsButtons buttons;
		private final UserData userData;
		private final IUserAndGroupsContainer container;

		@Override
		public MyGroupsButtons getFooter() {
			return buttons;
		}

		@Override
		public SashForm getEditor() {
			return sashForm;
		}

		public MyGroupsComposite(Composite parent, IMasterDetailSocial masterDetailSocial, final IUserAndGroupsContainer container, boolean showDialogs, final CardConfig cardConfig, final UserData userData, ICallback<String> showMyGroups, ICallback<IScrollableToId> postPopulate) {
			super(parent, cardConfig, GroupConstants.myGroupsCardType, JarAndPathConstants.myGroupsTitle, true);
			this.container = container;
			this.userData = userData;
			sashForm = new SashForm(getInnerBody(), SWT.HORIZONTAL);
			summaryTable = new Table(sashForm, SWT.FULL_SELECTION);
			buttons = new MyGroupsButtons(getInnerBody(), masterDetailSocial, cardConfig, container, showDialogs, userData, showMyGroups, new Callable<IdNameAndStatus>() {
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
			}, new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					return membershipTable.getItemCount();
				}
			});
			summaryTable.setHeaderVisible(true);
			new TableColumn(summaryTable, SWT.NULL).setText("Group Name");
			new TableColumn(summaryTable, SWT.NULL).setText("Members");
			new TableColumn(summaryTable, SWT.NULL).setText("My Status");

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
			setUpSummaryTableListener(container, stackLayout);
			membershipTable.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					sortOutButtonsEnabledStatus();
				}
			});
			sashForm.setWeights(new int[] { 2, 3 });

			populate(postPopulate);

		}

		private void populate(final ICallback<IScrollableToId> postPopulate) {
			container.accessWithCallbackFn(IGroupsReader.class, IUserMembershipReader.class, new IFunction2<IGroupsReader, IUserMembershipReader, List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> apply(final IGroupsReader groupsReader, IUserMembershipReader userMembershipReader) throws Exception {
					Iterable<Map<String, Object>> groups = userMembershipReader.walkGroupsFor(userData.softwareFmId, userData.crypto);
					List<Map<String, Object>> groupsWithName = Lists.map(groups, new IFunction1<Map<String, Object>, Map<String, Object>>() {
						@Override
						public Map<String, Object> apply(Map<String, Object> map) throws Exception {
							if (map.containsKey(CommonConstants.errorKey))
								return map;
							String groupId = (String) map.get(GroupConstants.groupIdKey);
							String groupCryptoKey = (String) map.get(GroupConstants.groupCryptoKey);
							try {
								String groupName = IGroupsReader.Utils.getGroupProperty(container, groupId, groupCryptoKey, GroupConstants.groupNameKey);
								return Maps.with(map, GroupConstants.groupNameKey, groupName, membershipCountKey, groupsReader.membershipCount(groupId, groupCryptoKey));
							} catch (Exception e) {
								return Maps.with(map, CommonConstants.errorKey, MessageFormat.format(GroupConstants.cannotDetermineGroupName, groupId, e));
							}
						}
					});

					return groupsWithName;
				}
			}, new ISwtFunction1<List<Map<String, Object>>, Void>() {
				@Override
				public Void apply(List<Map<String, Object>> groupsWithName) throws Exception {
					if (isDisposed())
						return null;
					for (Map<String, Object> map : Lists.sort(groupsWithName, Comparators.mapKey(GroupConstants.groupNameKey))) {
						if (map.containsKey(CommonConstants.errorKey)) {
							TableItem item = new TableItem(summaryTable, SWT.NULL);
							item.setText(new String[] { CommonMessages.corrupted, CommonMessages.record, "" });
						} else {
							String groupId = (String) map.get(GroupConstants.groupIdKey);
							String groupCryptoKey = (String) map.get(GroupConstants.groupCryptoKey);
							String groupName = (String) map.get(GroupConstants.groupNameKey);
							TableItem item = new TableItem(summaryTable, SWT.NULL);
							int membershipCount = (Integer) map.get(membershipCountKey);
							String membershipCountString = Integer.toString(membershipCount);
							String myStatus = Strings.nullSafeToString(map.get(GroupConstants.membershipStatusKey));
							IdNameAndStatus data = new IdNameAndStatus(groupId, groupName, myStatus);
							item.setData(data);
							item.setText(new String[] { groupName, membershipCountString, myStatus });
							idToCrypto.put(groupId, groupCryptoKey);
							Swts.packColumns(summaryTable, membershipTable);
						}
					}
					sortOutButtonsEnabledStatus();
					postPopulate.process(MyGroupsComposite.this);
					layout();
					return null;
				}

				@Override
				public String toString() {
					return "Swt function for MyGroupsComposite.populate";
				}
			});
		}

		private void setUpSummaryTableListener(final IUserAndGroupsContainer container, final StackLayout stackLayout) {
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
						final String groupId = idAndName.id;
						if (groupId == null)
							throw new NullPointerException("GroupId is null: " + Integer.toString(index));
						final String groupCryptoKey = idToCrypto.get(groupId);
						if (groupCryptoKey == null)
							throw new NullPointerException("GroupCrypto is null: " + Integer.toString(index));

						container.accessWithCallbackFn(IGroupsReader.class, new IFunction1<IGroupsReader, Iterable<Map<String, Object>>>() {
							@Override
							public Iterable<Map<String, Object>> apply(IGroupsReader from) throws Exception {
								return from.users(groupId, groupCryptoKey);
							}
						}, new ISwtFunction1<Iterable<Map<String, Object>>, Iterable<Map<String, Object>>>() {
							@Override
							public Iterable<Map<String, Object>> apply(Iterable<Map<String, Object>> from) throws Exception {
								for (Map<String, Object> user : Lists.sort(from, Comparators.mapKey(LoginConstants.emailKey))) {
									TableItem tableItem = new TableItem(membershipTable, SWT.NULL);
									if (user.containsKey(CommonConstants.errorKey))
										tableItem.setText(new String[] { CommonMessages.corrupted, CommonMessages.record });
									else {
										tableItem.setData(user);
										tableItem.setText(new String[] { Strings.nullSafeToString(user.get(LoginConstants.emailKey)), Strings.nullSafeToString(user.get(GroupConstants.membershipStatusKey)) });
									}
								}
								Swts.packColumns(membershipTable);
								rightHand.layout();
								sortOutButtonsEnabledStatus();
								return from;
							}

							@Override
							public String toString() {
								return "SwtFunction for summary table listener";
							}
						});
					}
				}
			});
		}

		private void sortOutButtonsEnabledStatus() {
			buttons.sortOutButtonStatus();
		}

		@Override
		public String toString() {
			return super.toString() + "/" + hashCode();
		}

		@Override
		public void selectAndScrollTo(String groupId) {
			for (int i = 0; i < summaryTable.getItemCount(); i++) {
				TableItem item = summaryTable.getItem(i);
				IdNameAndStatus idNameAndStatus = (IdNameAndStatus) item.getData();
				if (idNameAndStatus != null && idNameAndStatus.id.equals(groupId)) {
					Swts.selectOnlyAndNotifyListener(summaryTable, i);
					summaryTable.showItem(item);
				}
			}

		}

	}

	public MyGroups(Composite parent, IMasterDetailSocial masterDetailSocial, IUserAndGroupsContainer container, boolean showDialogs, CardConfig cardConfig, UserData userData, ICallback<String> showMyGroups, ICallback<IScrollableToId> postPopulate) {
		content = new MyGroupsComposite(parent, masterDetailSocial, container, showDialogs, cardConfig, userData, showMyGroups, postPopulate);
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