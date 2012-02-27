/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.DataComposite;
import org.softwareFm.swt.editors.DataCompositeWithFooterLayout;
import org.softwareFm.swt.editors.IDataCompositeWithFooter;
import org.softwareFm.swt.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.swt.swt.Swts;

public class MySoftwareFmLoggedIn implements IHasControl {

	private final MySoftwareFmLoggedInComposite content;

	public static class MySoftwareFmButtons implements IHasComposite {

		private final Composite content;
		public final Button logoutButton;
		public final Button changePasswordButton;

		public MySoftwareFmButtons(Composite parent, final ILoginDisplayStrategy loginDisplayStrategy, final IMySoftwareFmLoggedInStrategy loggedInStrategy, UserData userData) {
			this.content = new Composite(parent, SWT.NULL);
			final String email = userData.email();
			logoutButton = Swts.Buttons.makePushButton(content, "Logout", new Runnable() {
				@Override
				public void run() {
					loggedInStrategy.logout();
				}
			});
			changePasswordButton = Swts.Buttons.makePushButton(content, "Change Password", new Runnable() {
				@Override
				public void run() {
					loginDisplayStrategy.showChangePassword(email);
				}
			});
			content.setLayout(Swts.Row.getHorizonalNoMarginRowLayout());

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

	public static class MySoftwareFmLoggedInComposite extends DataComposite<Composite> implements IDataCompositeWithFooter<Composite, MySoftwareFmButtons> {

		private final Table userDetails;
		private final Composite mainComposite;
		private final MySoftwareFmButtons buttonComposite;
		public final Button myDataButton;
		public final Button myGroupsButton;

		public MySoftwareFmLoggedInComposite(Composite parent, CardConfig cardConfig, String cardType, final ILoginDisplayStrategy loginDisplayStrategy, final IMySoftwareFmLoggedInStrategy loggedInStrategy, UserData userData) {
			super(parent, cardConfig, cardType, SoftwareFmConstants.mySoftwareFmLoggedInTitle, true);
			mainComposite = new Composite(getInnerBody(), SWT.NULL);
			mainComposite.setBackground(getTitleSpec().background);
			this.userDetails = new Table(mainComposite, SWT.FULL_SELECTION);
			userDetails.setHeaderVisible(false);
			for (int i = 0; i < 2; i++)
				new TableColumn(userDetails, SWT.NULL);
			loggedInStrategy.userReader().refresh(userData.softwareFmId);
			for (String property : loggedInStrategy.displayProperties()) {
				TableItem item = new TableItem(userDetails, SWT.NULL);
				Object value = loggedInStrategy.userReader().getUserProperty(userData.softwareFmId, userData.crypto, property);
				String name = cardConfig.nameFn.apply(cardConfig, new LineItem(CardConstants.loginCardType, property, value));
				item.setText(new String[] { name, Strings.nullSafeToString(value) });
			}
			String softwareFmIdName = cardConfig.nameFn.apply(cardConfig, new LineItem(CardConstants.loginCardType, LoginConstants.softwareFmIdKey, null));
			TableItem softwareFmIdItem = new TableItem(userDetails, SWT.FULL_SELECTION);
			softwareFmIdItem.setText(new String[] { softwareFmIdName, userData.softwareFmId });

			myDataButton = Swts.Buttons.makePushButton(mainComposite, "My Data", new Runnable() {
				@Override
				public void run() {
					loggedInStrategy.showMyData();
				}
			});
			myGroupsButton = Swts.Buttons.makePushButton(mainComposite, "My Groups", new Runnable() {
				@Override
				public void run() {
					loggedInStrategy.showMyGroups();
				}
			});
			buttonComposite = new MySoftwareFmButtons(getInnerBody(), loginDisplayStrategy, loggedInStrategy, userData);

			Swts.packTables(userDetails);
			Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildren(mainComposite);
		}

		public MySoftwareFmButtons getButtonComposite() {
			return buttonComposite;
		}

		@Override
		public MySoftwareFmButtons getFooter() {
			return buttonComposite;
		}

		@Override
		public Composite getEditor() {
			return mainComposite;
		}

	}

	@SuppressWarnings("unused")
	public MySoftwareFmLoggedIn(Composite parent, final CardConfig cardConfig, String title, String text, final UserData userData, final ILoginDisplayStrategy loginDisplayStrategy, final IMySoftwareFmLoggedInStrategy loggedInStrategy) {
		content = new MySoftwareFmLoggedInComposite(parent, cardConfig, CardConstants.loginCardType, loginDisplayStrategy, loggedInStrategy, userData);
		content.setLayout(new DataCompositeWithFooterLayout());
	}

	@Override
	public Control getControl() {
		return content;
	}

	public static void main(String[] args) {
		Swts.Show.display(MySoftwareFmLoggedIn.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(from.getDisplay());
				return (Composite) new MySoftwareFmLoggedIn(from, cardConfig, CardConstants.loggedInTitle, CardConstants.loggedInText, //
						new UserData("email", "my softwarefm id", null),//
						ILoginDisplayStrategy.Utils.sysoutDisplayStrategy(),//
						IMySoftwareFmLoggedInStrategy.Utils.sysout(LoginConstants.emailKey, "my email", LoginConstants.monikerKey, "my moniker")).getControl();
			}
		});
	}
}