package org.arc4eclipse.displayTest;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactoryBuilder;
import org.arc4eclipse.displayText.DisplayText;
import org.arc4eclipse.panel.ISelectedBindingManager;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.swt.widgets.Composite;

public class SmokeTestDisplayContainer {
	public static void main(String[] args) {
		final IDisplayContainerFactoryBuilder managerFactory = IDisplayContainerFactoryBuilder.Utils.displayManager();
		managerFactory.registerDisplayer(new DisplayText());
		managerFactory.registerForEntity("entity", "text_one", "One");
		managerFactory.registerForEntity("entity", "text_two", "Two");
		managerFactory.registerForEntity("entity", "text_three", "Three");
		final IDisplayContainerFactory factory = managerFactory.build();
		final IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();

		Swts.display("DisplayContainer1", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				DisplayerContext displayerContext = new DisplayerContext(IImageFactory.Utils.imageFactory(), ISelectedBindingManager.Utils.noSelectedBindingManager(), repository);
				final IDisplayContainer displayContainer = factory.create(displayerContext, from, "entity");

				final BindingContext bindingContext1 = makeBindingContext(from, "entity", 1, "text_one", "value1");
				displayContainer.setValues(bindingContext1);
				System.out.println(Swts.layoutAsString(from));
				System.out.println();
				from.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						BindingContext bindingContext2 = makeBindingContext(from, "entity", 2, "text_two", "value2");
						displayContainer.setValues(bindingContext2);
						System.out.println(Swts.layoutAsString(from));
					}
				});
				return displayContainer.getComposite();
			}

			private BindingContext makeBindingContext(Composite parent, String entity, int count, Object... dataAsObject) {
				return new BindingContext(repository, new Images(parent.getDisplay()), "url", //
						Maps.<String, Object> makeMap(dataAsObject), //
						Maps.<String, Object> makeMap(RepositoryConstants.entity, entity, "context", "c" + count));
			}
		});
		repository.shutdown();
	}
}
