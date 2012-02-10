package org.softwareFm.eclipse.mysoftwareFm;

import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.LocalGroupsReader;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.eclipse.user.UserMembershipReaderForLocal;
import org.softwareFm.swt.card.composites.TextInCompositeWithCardMargin;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;

public class MyGroups implements IHasComposite {
	public static IShowMyGroups showMyGroups(final IServiceExecutor executor, final CardConfig cardConfig, final IMasterDetailSocial masterDetailSocial, final IUrlGenerator userUrlGenerator, final IUrlGenerator groupUrlGenerator, final IGitLocal gitLocal) {
		return new IShowMyGroups() {
			@Override
			public void show(final UserData userData) {
				executor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						final IUserReader user = IUserReader.Utils.localUserReader(gitLocal, userUrlGenerator);
						String membershipCrypto = user.getUserProperty(userData.softwareFmId, userData.crypto, GroupConstants.membershipCryptoKey);
						final IGroupsReader groupsReader = new LocalGroupsReader(groupUrlGenerator, gitLocal);
						if (membershipCrypto == null) {
							masterDetailSocial.createAndShowDetail(new IFunction1<Composite, TextInCompositeWithCardMargin>() {
								@Override
								public TextInCompositeWithCardMargin apply(Composite from) throws Exception {
									TextInCompositeWithCardMargin result = new TextInCompositeWithCardMargin(from, SWT.WRAP | SWT.READ_ONLY, cardConfig);
									result.setText("You belong to no groups");
									return result;
								}
							});
							return null;
						}
						final IUserMembershipReader userMembershipReader = new UserMembershipReaderForLocal(LoginConstants.userGenerator(), gitLocal, user, userData.crypto);
						Swts.asyncExec(masterDetailSocial.getControl(), new Runnable() {
							@Override
							public void run() {
								masterDetailSocial.hideSocial();
								masterDetailSocial.createAndShowDetail(new IFunction1<Composite, MyGroups>() {
									@Override
									public MyGroups apply(Composite from) throws Exception {
										return new MyGroups(from, userMembershipReader, groupsReader, userData.softwareFmId);
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

	public static class MyGroupsComposite extends Composite {
		private final Table table;
		private final IGroupsReader groupsReader;

		public MyGroupsComposite(Composite parent, IUserMembershipReader membershipReader, IGroupsReader groupReaders, String softwareFmId) {
			super(parent, SWT.NULL);
			this.groupsReader = groupReaders;
			table = new Table(this, SWT.FULL_SELECTION);
			table.setHeaderVisible(true);
			new TableColumn(table, SWT.NULL).setText("Group Name");
			new TableColumn(table, SWT.NULL).setText("Members");
			for (Map<String, Object> map : membershipReader.walkGroupsFor(softwareFmId)) {
				String groupId = (String) map.get(GroupConstants.groupIdKey);
				String groupCryptoKey = (String) map.get(GroupConstants.groupCryptoKey);
				String groupName = groupReaders.getGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey);
				TableItem item = new TableItem(table, SWT.NULL);
				int membershipCount = groupsReader.membershipCount(groupId, groupCryptoKey);
				String membershipCountString = Integer.toString(membershipCount);
				item.setText(new String[] { groupName, membershipCountString });
			}
			for (int i = 0; i<table.getColumnCount(); i++)
				table.getColumn(i).pack();
			table.pack();
		}
	}

	public MyGroups(Composite parent, IUserMembershipReader membershipReader, IGroupsReader groupsReader, String softwareFmId) {
		content = new MyGroupsComposite(parent, membershipReader, groupsReader, softwareFmId);
		content.setLayout(new FillLayout());
		Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildrenWithLastGrabingVertical(content);
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
