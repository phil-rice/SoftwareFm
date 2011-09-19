package org.softwarefm.display;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwarefm.display.fixture.SoftwareFmFixture;

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
