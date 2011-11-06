package org.softwareFm.explorer.eclipse;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.internal.BasicCardConfigurator;
import org.softwareFm.card.internal.CardExplorer;
import org.softwareFm.display.data.IGuiDataListener;
import org.softwareFm.eclipse.SoftwareFmActivator;
import org.softwareFm.repositoryFacard.IRepositoryFacard;

public class ExplorerView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		final String rootUrl = "/softwareFm/data";
		CardConfig cardConfig = new BasicCardConfigurator().configure(parent.getDisplay(), new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.repositoryCardDataStore(parent, IRepositoryFacard.Utils.defaultFacardForCardExplorer())));
		final CardExplorer cardExplorer = new CardExplorer(parent, cardConfig, rootUrl);
		cardExplorer.setUrl(rootUrl + "/org");
		SoftwareFmActivator softwareFmActivator = SoftwareFmActivator.getDefault();
		softwareFmActivator.getSelectedBindingManager();//creates it
		softwareFmActivator.getGuiDataStore().addGuiDataListener(new IGuiDataListener() {
			@Override
			public void data(String entity, String url) {
				if (!entity.equals("artifact") || url == null)
					return;
				cardExplorer.setUrl(url);
			}
		});
	}

	@Override
	public void setFocus() {
	}

}
