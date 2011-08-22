package org.arc4eclipse.displayCore.api;

import java.util.ResourceBundle;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.panel.ISelectedBindingManager;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.swtBasics.images.Resources;
import org.arc4eclipse.swtBasics.text.ConfigForTitleAnd;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.maps.Maps;
import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class Displayers {
	public static void displayWithKey1(final IDisplayer<?, ?> displayer, final ResourceBundle bundle, final Object data) {
		display(displayer, bundle, data, Displayers.class, "Key1");
	}

	public static void display(final IDisplayer<?, ?> displayer, final ResourceBundle bundle, final Object data, final Class<?> imageAnchor, final String key) {
		Swts.display(displayer.getClass().getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				IDisplayContainer container = makeContainer(displayer, IEditor.Utils.noEditor(), bundle, from, "entity", imageAnchor, key);
				BindingContext bindingContext = new BindingContext("someUrl", Maps.<String, Object> makeMap(key, data), Maps.<String, Object> makeMap(RepositoryConstants.entity, "entity"));
				container.setValues(bindingContext);
				return container.getComposite();
			}
		});
	}

	public static IDisplayContainer makeContainerForKey1(final IDisplayer<?, ?> displayer, IEditor editor, final ResourceBundle bundle, Composite from) {
		return makeContainer(displayer, editor, bundle, from, "entity", Displayers.class, "Key1");
	}

	public static IDisplayContainer makeContainer(final IDisplayer<?, ?> displayer, IEditor editor, final ResourceBundle bundle, Composite from, String entity, Class<?> imageAnchor, String key) {
		Display display = from.getDisplay();
		ImageRegistry imageRegistry = Images.withBasics(display);
		Images.registerImages(from.getDisplay(), imageRegistry, imageAnchor, key);
		IResourceGetter resourceGetter = Resources.builderWithBasics().register(bundle).build();
		DisplayerContext context = new DisplayerContext(//
				ISelectedBindingManager.Utils.noSelectedBindingManager(), //
				IArc4EclipseRepository.Utils.repository(), //
				ConfigForTitleAnd.create(display, resourceGetter, imageRegistry));

		IDisplayContainerFactoryBuilder factoryBuilder = IDisplayContainerFactoryBuilder.Utils.factoryBuilder(imageRegistry);
		factoryBuilder.registerDisplayer("displayer", displayer);
		factoryBuilder.registerEditor("editor", editor);
		IDisplayContainerFactory factory = factoryBuilder.build(entity);
		factory.register(Maps.<String, String> makeMap(DisplayCoreConstants.key, key, DisplayCoreConstants.displayer, "displayer", DisplayCoreConstants.editor, "editor"));
		IDisplayContainer container = factory.create(context, from);
		return container;
	}
}
