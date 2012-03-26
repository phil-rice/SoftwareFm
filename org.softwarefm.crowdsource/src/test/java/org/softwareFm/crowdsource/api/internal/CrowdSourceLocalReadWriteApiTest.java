package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.git.IRepoFinder;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public class CrowdSourceLocalReadWriteApiTest extends AbstractCrowdReadWriterApiTest {

	public void testExtras(){
		IFunction1<IRepoFinder, Integer> repoFinderFn = Functions.<IRepoFinder>ensureSameParameters();
		getApi().makeContainer().access(IRepoFinder.class, repoFinderFn);
		getApi().makeContainer().access(IRepoFinder.class, repoFinderFn);
	}
	
	@Override
	protected ICrowdSourcedApi getApi() {
		return super.getLocalApi();
	}

}
