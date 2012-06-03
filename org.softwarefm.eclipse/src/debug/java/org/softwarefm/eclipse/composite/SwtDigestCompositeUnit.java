package org.softwarefm.eclipse.composite;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.utilities.functions.IFunction2;

public class SwtDigestCompositeUnit<C extends SoftwareFmComposite> {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		new SoftwareFmCompositeUnit<SwtDigestComposite>(SwtDigestComposite.class.getSimpleName(), new IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SwtDigestComposite>() {
			public SwtDigestComposite apply(Composite parent, SoftwareFmContainer<Map<String, Object>> container) throws Exception {
				return new SwtDigestComposite(parent, container);
			}
		});
	}
}
