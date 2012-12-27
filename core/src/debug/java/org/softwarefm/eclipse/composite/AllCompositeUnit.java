package org.softwarefm.core.composite;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.utilities.functions.IFunction2;

public class AllCompositeUnit {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		new SoftwareFmCompositeUnit("Class And Method", new IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, AllComposite>() {
			public AllComposite apply(Composite parent, SoftwareFmContainer<Map<String, Object>> container) throws Exception {
				return new AllComposite(parent, container);
			}
		});
	}
}
