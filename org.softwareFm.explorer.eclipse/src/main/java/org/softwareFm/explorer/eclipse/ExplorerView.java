package org.softwareFm.explorer.eclipse;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.internal.BasicCardConfigurator;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.display.data.IUrlGeneratorMap;
import org.softwareFm.display.swt.Swts;
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
		final Activator activator = Activator.getDefault();

		final String rootUrl = "/softwareFm/data";
		IRepositoryFacard repository = activator.getRepository();
		final CardConfig cardConfig = new BasicCardConfigurator().configure(parent.getDisplay(), new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.repositoryCardDataStore(parent, repository)));
		MasterDetailSocial masterDetailSocial = new MasterDetailSocial(parent, SWT.NULL);
		Swts.resizeMeToParentsSize(masterDetailSocial.getControl());
		final Explorer explorer = new Explorer(cardConfig, rootUrl, masterDetailSocial, activator.getServiceExecutor());
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
			public void selectionOccured(final BindingRipperResult ripperResult) {
				String hexDigest = ripperResult.hexDigest;
				IUrlGenerator jarUrlGenerator = urlGeneratorMap.get("urlGenerator.jar");
				String url = jarUrlGenerator.findUrlFor(Maps.stringObjectMap(JdtConstants.hexDigestKey, hexDigest));
				System.out.println("Digest: " + hexDigest + "\n Url: " + url);
				cardConfig.cardDataStore.processDataFor(url, new ICardDataStoreCallback<Void>() {
					@Override
					public Void process(String url, Map<String, Object> result) throws Exception {
						IUrlGenerator cardUrlGenerator = urlGeneratorMap.get("urlGenerator.artifact");
						String cardUrl = cardUrlGenerator.findUrlFor(result);
						explorer.displayCard(cardUrl);
						return null;
					}

					@Override
					public Void noData(String url) throws Exception {
						explorer.displayUnrecognisedJar(url, ripperResult.path.toOSString());
						
						return null;
					}
				});
			}
		});

	}

	@Override
	public void setFocus() {
	}

}
