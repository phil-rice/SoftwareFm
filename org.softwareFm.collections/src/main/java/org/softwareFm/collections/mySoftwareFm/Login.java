package org.softwareFm.collections.mySoftwareFm;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardTable;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.display.okCancel.OkCancel;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Buttons;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class Login implements ILogin {

	public static class LoginComposite extends Composite {
		final ICardTable cardTable;
		private final OkCancel okCancel;

		public LoginComposite(Composite parent, CardConfig cardConfig, final ILoginStrategy loginStrategy) {
			super(parent, SWT.NULL);
			String cardType = CollectionConstants.mySoftwareFmCardType;
			Map<String, Object> data = Maps.stringObjectMap(CardConstants.slingResourceType, cardType, CollectionConstants.email, "", CollectionConstants.password, "", CollectionConstants.confirmPassword, "");
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, ICardData.Utils.create(cardConfig, cardType, "", data));
			cardTable = ICardTable.Utils.cardTable(this, cardConfig, titleSpec, cardType, data);

			IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, cardType);
			Buttons.makePushButton(this, resourceGetter, CollectionConstants.signUp, new Runnable() {
				@Override
				public void run() {
					loginStrategy.signUp();

				}
			});
			Buttons.makePushButton(this, resourceGetter, CollectionConstants.forgotPassword, new Runnable() {
				@Override
				public void run() {
					loginStrategy.forgotPassword();

				}
			});
			okCancel = new OkCancel(this, resourceGetter, new Runnable() {
				@Override
				public void run() {
					assert getPassword().equals(getConfirmPassword());
					assert getPassword().length() > 0;
					loginStrategy.login(getEmail(), getPassword());

				}

				private String getEmail() {
					return Swts.getStringFor(cardTable.getTable(), CollectionConstants.email);
				}

				private String getPassword() {
					return Swts.getStringFor(cardTable.getTable(), CollectionConstants.password);
				}

				private String getConfirmPassword() {
					return Swts.getStringFor(cardTable.getTable(), CollectionConstants.confirmPassword);
				}
			}, new Runnable() {
				@Override
				public void run() {
					loginStrategy.cancelLogin();
				}
			});

		}
	}

	private final LoginComposite content;

	public Login(Composite parent, CardConfig cardConfig, ILoginStrategy loginStrategy) {
		content = new LoginComposite(parent, cardConfig, loginStrategy);
		Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildren(content);

	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public static void main(String[] args) {
		Swts.Show.display(Login.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite parent) throws Exception {
				CardConfig cardConfig = new ICardConfigurator.Utils().basicConfigurator().configure(parent.getDisplay(), new CardConfig(ICardFactory.Utils.noCardFactory(), ICardDataStore.Utils.mock()));
				return (Composite) new Login(parent, cardConfig, null).getControl();
			}
		});
	}

}
