package org.softwareFm.eclipse.mysoftwareFm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
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
import org.softwareFm.swt.card.composites.TextInCompositeWithCardMargin;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;

public class MyGroups implements IHasComposite {
	public static IShowMyGroups showMyGroups(final IServiceExecutor executor, final CardConfig cardConfig, final IMasterDetailSocial masterDetailSocial, final IUrlGenerator userUrlGenerator, final IUrlGenerator groupUrlGenerator, final IGitLocal gitLocal, final IProjectTimeGetter projectTimeGetter, final IRequestGroupReportGeneration reportGenerator) {
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
										return new MyGroups(from, cardConfig, userMembershipReader, groupsReader, userData.softwareFmId, projectTimeGetter, reportGenerator);
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

	public static class MyGroupsComposite extends CompositeWithCardMargin {
		private final Table summaryTable;
		private final IGroupsReader groupsReader;
		private final SashForm sashForm;
		private final Table groupTable;
		private final Map<String, String> idToCrypto = Maps.newMap();

		public MyGroupsComposite(Composite parent, final CardConfig cc, IUserMembershipReader membershipReader, final IGroupsReader groupReaders, String softwareFmId, IProjectTimeGetter projectTimeGetter, final IRequestGroupReportGeneration reportGenerator) {
			super(parent, SWT.NULL, cc);
			addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					Rectangle ca = getClientArea();
					e.gc.drawRoundRectangle(ca.x - cc.cornerRadiusComp, ca.y - cc.cornerRadiusComp, ca.width + 2 * cc.cornerRadiusComp, ca.height + 2 * cc.cornerRadiusComp, cc.cornerRadius, cc.cornerRadius);
				}
			});
			this.groupsReader = groupReaders;
			sashForm = new SashForm(this, SWT.HORIZONTAL);
			summaryTable = new Table(sashForm, SWT.FULL_SELECTION);
			summaryTable.setHeaderVisible(true);
			new TableColumn(summaryTable, SWT.NULL).setText("Group Name");
			new TableColumn(summaryTable, SWT.NULL).setText("Members");
			for (Map<String, Object> map : membershipReader.walkGroupsFor(softwareFmId)) {
				String groupId = (String) map.get(GroupConstants.groupIdKey);
				String groupCryptoKey = (String) map.get(GroupConstants.groupCryptoKey);
				String groupName = groupReaders.getGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey);
				TableItem item = new TableItem(summaryTable, SWT.NULL);
				item.setData(groupId);
				int membershipCount = groupsReader.membershipCount(groupId, groupCryptoKey);
				String membershipCountString = Integer.toString(membershipCount);
				item.setText(new String[] { groupName + "/" + groupId, membershipCountString });
				idToCrypto.put(groupId, groupCryptoKey);
			}

			groupTable = new Table(sashForm, SWT.FULL_SELECTION);
			groupTable.setHeaderVisible(true);
			final Iterable<String> lastNMonths = projectTimeGetter.lastNMonths(3);
			new TableColumn(groupTable, SWT.NULL).setText("Group Id");
			new TableColumn(groupTable, SWT.NULL).setText("ArtifactId Id");
			for (String month : lastNMonths) {
				LineItem lineItem = new LineItem(GroupConstants.myGroupsCardType, month, null);
				String name = cc.nameFn.apply(cc, lineItem);
				new TableColumn(groupTable, SWT.FULL_SELECTION).setText(name);
			}
			summaryTable.addListener(SWT.Selection, new Listener() {
				@Override
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public void handleEvent(Event event) {
					groupTable.removeAll();
					int index = summaryTable.getSelectionIndex();
					if (index >= 0) {
						TableItem item = summaryTable.getItem(index);
						String groupId = (String) item.getData();
						if (groupId == null)
							throw new NullPointerException(Integer.toString(index));
						String groupCryptoKey = idToCrypto.get(groupId);
						if (groupCryptoKey == null)
							throw new NullPointerException(groupCryptoKey);
						Map<String, Map<String, Map<String, Integer>>> map = Maps.newMap();
						for (String month : lastNMonths) {
							reportGenerator.request(groupId, groupCryptoKey, month);
							Map<String, Map<String, Map<String, List<Integer>>>> report = (Map) groupReaders.getUsageReport(groupId, groupCryptoKey, month);
							if (report != null)
								for (Entry<String, Map<String, Map<String, List<Integer>>>> groupEntry : report.entrySet())
									for (Entry<String, Map<String, List<Integer>>> artifactEntry : groupEntry.getValue().entrySet())
										Maps.addToMapOfMapOfMaps(map, HashMap.class, groupEntry.getKey(), artifactEntry.getKey(), month, artifactEntry.getValue().size());
						}
						for (Entry<String, Map<String, Map<String, Integer>>> groupEntry : map.entrySet())
							for (Entry<String, Map<String, Integer>> artifactEntry : groupEntry.getValue().entrySet()) {
								TableItem reportItem = new TableItem(groupTable, SWT.FULL_SELECTION);
								reportItem.setText(0, groupEntry.getKey());
								reportItem.setText(1, artifactEntry.getKey());
								int i = 2;
								for (String month : lastNMonths)
									reportItem.setText(i++, Strings.nullSafeToString(artifactEntry.getValue().get(month)));
							}
					}
					Swts.packColumns(groupTable);
				}
			});
			Swts.packTables(summaryTable, groupTable);
			sashForm.setWeights(new int[] { 2, 3 });
		}
	}

	public MyGroups(Composite parent, CardConfig cardConfig, IUserMembershipReader membershipReader, IGroupsReader groupsReader, String softwareFmId, IProjectTimeGetter projectTimeGetter, IRequestGroupReportGeneration reportGenerator) {
		content = new MyGroupsComposite(parent, cardConfig, membershipReader, groupsReader, softwareFmId, projectTimeGetter, reportGenerator);
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
