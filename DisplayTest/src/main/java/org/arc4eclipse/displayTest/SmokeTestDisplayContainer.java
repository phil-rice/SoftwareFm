package org.arc4eclipse.displayTest;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.displayCore.api.IModifiesToBeDisplayed;
import org.arc4eclipse.displayCore.api.ITitleLookup;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.swt.widgets.Composite;

public class SmokeTestDisplayContainer {
	public static void main(String[] args) {
		final IDisplayManager manager = IDisplayManager.Utils.displayManager();
		manager.registerDisplayer(IManyDisplayers.Utils.textDisplay());
		manager.addModifier(IModifiesToBeDisplayed.Utils.addAllways("added", "value"));
		Swts.display("DisplayContainer1", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				final Map<String, Object> context = Maps.<String, Object> makeMap("context", "c1");
				final Map<String, Object> jarData1 = Maps.<String, Object> makeMap("entity", "jarData");
				final Images images = new Images(from.getDisplay());
				final BindingContext bindingContext = new BindingContext(IArc4EclipseRepository.Utils.repository(), ITitleLookup.Utils.titleLookup(), images, "url", jarData1, context);
				final IDisplayContainer displayContainer = IDisplayContainer.Utils.displayContainer(from);
				manager.populate(displayContainer, bindingContext);
				from.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						Map<String, Object> jarData = Maps.makeMap("unknown:name2", "value22", "text:name1", "value11");
						final BindingContext bindingContext = new BindingContext(IArc4EclipseRepository.Utils.repository(), ITitleLookup.Utils.titleLookup(), images, "url", jarData, context);
						manager.populate(displayContainer, bindingContext);
					}
				});
				return displayContainer.getComposite();
			}
		});
	}
}
