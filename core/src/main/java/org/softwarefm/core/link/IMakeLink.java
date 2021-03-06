package org.softwarefm.core.link;

import org.apache.maven.model.Model;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.link.internal.MakeLink;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.core.templates.ITemplateStore;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.utilities.cache.IHasCache;
import org.softwarefm.utilities.callbacks.ICallback2;

public interface IMakeLink {

	/** Adds the link to software fm: the digest will now point to groupid/artifactid/version */
	void makeDigestLink(ArtifactData artifactData);

	/** The model can be null if not known */
	void populateProjectIfBlank(ArtifactData artifactData, Model model);

	public static class Utils {
		/**
		 * The client should be set up with the host/port parameters
		 * 
		 * @param templateStore
		 */
		public static IMakeLink makeLink(IUrlStrategy urlStrategy, IHasCache cache) {
			return new MakeLink(urlStrategy, ITemplateStore.Utils.templateStore(urlStrategy), cache);
		}

		public static IMakeLink makeLink(IUrlStrategy urlStrategy, ITemplateStore templateStore, IHasCache cache) {
			return new MakeLink(urlStrategy, templateStore, cache);
		}

		/** This is for helper tools like SoftwareFmCompositeUnit */
		public static <S> ICallback2<ArtifactData, Integer> manuallyImportWhenNotInEclipse(final IMakeLink makeLink, final ISelectedBindingManager<S> selectedBindingManager) {
			return new ICallback2<ArtifactData, Integer>() {
				public void process(ArtifactData artifactData, Integer thisSelectionId) throws Exception {
					if (thisSelectionId == selectedBindingManager.currentSelectionId()) {
						makeLink.makeDigestLink(artifactData);
						selectedBindingManager.reselect(thisSelectionId);
					}
				}
			};
		}
	}
}
