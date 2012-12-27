package org.softwarefm.core.composite;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.utilities.functions.IFunction2;

public class DigestCompositeUnit<C extends SoftwareFmComposite> {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		new SoftwareFmCompositeUnit<DigestComposite>(DigestComposite.class.getSimpleName(), new IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, DigestComposite>() {
			public DigestComposite apply(Composite parent, SoftwareFmContainer<Map<String, Object>> container) throws Exception {
				return new DigestComposite(parent, container);
			}
		});
	}
}
