package org.softwareFm.eclipse.sample;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.configuration.fixture.SoftwareFmFixture;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.services.IServiceExecutor;

public class Sample {

	public static void main(String[] args) {
		final IServiceExecutor service = IServiceExecutor.Utils.defaultExecutor();
		try {
			Show.display("Sample", new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					return new SoftwareFmFixture(from.getDisplay(), service).makeComposite(from).getComposite();
				}
			});
		} finally {
			service.shutdown();
		}
	}
}
