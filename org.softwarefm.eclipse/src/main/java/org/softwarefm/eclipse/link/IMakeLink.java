package org.softwarefm.eclipse.link;

import org.apache.maven.model.Model;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.link.internal.MakeLink;
import org.softwarefm.eclipse.templates.ITemplateStore;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.maps.IHasCache;

public interface IMakeLink {

	/** Adds the link to software fm: the digest will now point to groupid/artifactid/version */
	void makeDigestLink(ProjectData projectData);

	/** The model can be null if not known */
	void populateProjectIfBlank(ProjectData projectData, Model model);

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

		public static ICallback<ProjectData> manuallyImport(final IMakeLink makeLink) {
			return new ICallback<ProjectData>() {
				public void process(ProjectData projectData) throws Exception {
					makeLink.makeDigestLink(projectData);
				}
			};
		}
	}
}
