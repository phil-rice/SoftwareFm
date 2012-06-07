package org.softwarefm.eclipse.link;

import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.link.internal.MakeLink;

public interface IMakeLink {

	/** Adds the link to software fm: the digest will now point to groupid/artifactid/version */
	void makeLink(ProjectData projectData);
	
	public static class Utils {
		/** The client should be set up with the host/port parameters */
		public static IMakeLink makeLink(){
			return new MakeLink();
		}
	}
}
