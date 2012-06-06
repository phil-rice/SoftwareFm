package org.softwarefm.eclipse.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.resources.IResourceGetter;

public class MavenImportJob implements ICallback<String> {

	private final IResourceGetter resourceGetter;
	
	
	public MavenImportJob(IResourceGetter resourceGetter) {
		this.resourceGetter = resourceGetter;
	}


	public void process(String t) throws Exception {
		Job job = new Job(IResourceGetter.Utils.getMessageOrException(resourceGetter, SwtConstants.mavenImportKey, t)){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return null;
			}
			
		};
		
	}

}
