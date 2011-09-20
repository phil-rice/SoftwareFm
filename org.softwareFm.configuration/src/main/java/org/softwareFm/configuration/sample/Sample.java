package org.softwareFm.configuration.sample;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.configuration.fixture.SoftwareFmFixture;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.functions.IFunction1;

public class Sample {

	public static void main(String[] args) {
		Swts.display("Sample", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				return new SoftwareFmFixture(from.getDisplay()).makeComposite(from).getComposite();
			}
		});
	}
}
