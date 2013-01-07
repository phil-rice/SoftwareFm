package org.softwarefm.eclipse.views;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.composite.DebugTextComposite;
import org.softwarefm.eclipse.SoftwareFmPlugin;

public class DebugTextView extends SoftwareFmView<DebugTextComposite> {

	@Override
	public DebugTextComposite makeSoftwareFmComposite(Composite parent, SoftwareFmContainer<?> container) {
		DebugTextComposite debugTextComposite = new DebugTextComposite(parent, container);
		debugTextComposite.setViewGetter(new Callable<Map<Object, List<Object>>>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Map<Object, List<Object>> call() throws Exception {
				return (Map) SoftwareFmPlugin.getDefault().getViews();
			}
		});
		SoftwareFmPlugin.getDefault().setLogger(debugTextComposite.logger());
		return debugTextComposite;
	}

	@Override
	public void dispose() {
		SoftwareFmPlugin.getDefault().setLogger(null);
		super.dispose();
	}

}
