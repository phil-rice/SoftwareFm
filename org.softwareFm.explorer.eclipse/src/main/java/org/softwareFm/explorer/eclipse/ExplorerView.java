package org.softwareFm.explorer.eclipse;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.internal.BasicCardConfigurator;
import org.softwareFm.card.internal.CardExplorer;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.display.data.IUrlGeneratorMap;
import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.eclipse.ISelectedBindingListener;
import org.softwareFm.eclipse.ISelectedBindingManager;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.JdtConstants;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.maps.Maps;

public class ExplorerView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		final String rootUrl = "/softwareFm/data";
		CardConfig cardConfig = new BasicCardConfigurator().configure(parent.getDisplay(), new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.repositoryCardDataStore(parent, IRepositoryFacard.Utils.defaultFacardForCardExplorer())));
		final CardExplorer cardExplorer = new CardExplorer(parent, cardConfig, rootUrl);
		cardExplorer.setUrl(rootUrl + "/org");
		final Activator activator = Activator.getDefault();
		ISelectedBindingManager selectedBindingManager = activator.getSelectedBindingManager();// creates it
		String prefix = "/softwareFm/data/";

		final IUrlGeneratorMap urlGeneratorMap = IUrlGeneratorMap.Utils.urlGeneratorMap(//
				"urlGenerator.group", new UrlGenerator(prefix + "{3}/{2}", "groupId"),// hash, hash, groupId, groundIdWithSlash
				"urlGenerator.artifact", new UrlGenerator(prefix + "{3}/{2}/artifact/{4}", "groupId", "artifactId"),// 0,1: hash, 2,3: groupId, 4,5: artifactId
				"urlGenerator.version", new UrlGenerator(prefix + "{3}/{2}/artifact/{4}/version/{6}", "groupId", "artifactId", "version"),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
				"urlGenerator.digest", new UrlGenerator(prefix + "{3}/{2}/artifact/{4}/version/{6}/digest/{8}", "groupId", "artifactId", "version", JdtConstants.hexDigestKey),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version, 8,9: digest
				"urlGenerator.jar", new UrlGenerator("/softwareFm/jars/{0}/{1}/{2}", JdtConstants.hexDigestKey),// 0,1: hash, 2,3: digest
				"urlGenerator.user", new UrlGenerator("/softwareFm/users/guid/{0}/{1}/{2}", "notSure"));// hash and guid

		selectedBindingManager.addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
			@Override
			public void selectionOccured(BindingRipperResult ripperResult) {
				String hexDigest = ripperResult.hexDigest;
				IRepositoryFacard repository = activator.getRepository();
				IUrlGenerator generator = urlGeneratorMap.get("urlGenerator.jar");
				String url = generator.findUrlFor(Maps.stringObjectMap(JdtConstants.hexDigestKey, hexDigest));
				System.out.println("Digest: " + hexDigest +"\n Url: " + url);
			}
		});

	}

	@Override
	public void setFocus() {
	}

}
