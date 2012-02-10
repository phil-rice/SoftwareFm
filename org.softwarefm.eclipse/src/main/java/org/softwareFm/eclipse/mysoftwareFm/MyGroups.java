package org.softwareFm.eclipse.mysoftwareFm;

import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IUserReader;
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
	public static IShowMyGroups showMyGroups(final IServiceExecutor executor, final CardConfig cardConfig, final IMasterDetailSocial masterDetailSocial, final IUrlGenerator userUrlGenerator, final IGitLocal gitLocal) {
		return new IShowMyGroups() {
			@Override
			public void show(final UserData userData) {
				executor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						final IUserReader user = IUserReader.Utils.localUserReader(gitLocal, userUrlGenerator);
						String membershipCrypto = user.getUserProperty(userData.softwareFmId, userData.crypto, GroupConstants.membershipCryptoKey);
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
						final IUserMembershipReader userMembershipReader = new UserMembershipReaderForLocal(LoginConstants.userGenerator(), gitLocal, user, membershipCrypto);
						Swts.asyncExec(masterDetailSocial.getControl(), new Runnable() {
							@Override
							public void run() {
								masterDetailSocial.hideSocial();
								masterDetailSocial.createAndShowDetail(new IFunction1<Composite, MyGroups>() {
									@Override
									public MyGroups apply(Composite from) throws Exception {
										return new MyGroups(from, userMembershipReader, userData.softwareFmId);
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
		private final StyledText text;

		public MyGroupsComposite(Composite parent, IUserMembershipReader membershipReader, String softwareFmId) {
			super(parent, SWT.NULL);
			text = new StyledText(this, SWT.WRAP | SWT.READ_ONLY);
			for (Map<String, Object> map : membershipReader.walkGroupsFor(softwareFmId))
				text.append(map.toString() + "\n");
		}
	}

	public MyGroups(Composite parent, IUserMembershipReader membershipReader, String softwareFmId) {
		content = new MyGroupsComposite(parent, membershipReader, softwareFmId);
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
