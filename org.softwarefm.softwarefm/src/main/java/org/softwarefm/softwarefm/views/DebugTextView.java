package org.softwarefm.softwarefm.views;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.DebugTextComposite;
import org.softwarefm.softwarefm.SoftwareFmActivator;

public class DebugTextView extends SoftwareFmView<DebugTextComposite> {

	@Override
	public DebugTextComposite makeSoftwareFmComposite(Composite parent, SoftwareFmContainer<?> container) {
		DebugTextComposite debugTextComposite = new DebugTextComposite(parent, container);
		debugTextComposite.setViewGetter(new Callable<Map<Object,List<Object>>>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Map<Object, List<Object>> call() throws Exception {
				return (Map)SoftwareFmActivator.getDefault().getViews();
			}
		});
		SoftwareFmActivator.getDefault().setLogger(debugTextComposite.logger());
		return debugTextComposite;
	}

}
