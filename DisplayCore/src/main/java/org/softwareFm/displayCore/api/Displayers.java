package org.softwareFm.displayCore.api;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.panel.ISelectedBindingManager;
import org.softwareFm.repository.api.ISoftwareFmRepository;
import org.softwareFm.repository.api.IUrlGeneratorMap;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.softwareFmImages.IImageRegister;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class Displayers {
	public static void displayWithKey1(final IDisplayer<?, ?> displayer, final IResourceGetter resourceGetter, final Object data) {
		display(displayer, resourceGetter, data, Displayers.class, "Key1");
	}

	public static void display(final IDisplayer<?, ?> displayer, final IResourceGetter resourceGetter, final Object data, final Class<?> imageAnchor, final String key) {
		Swts.display(displayer.getClass().getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				IDisplayContainer container = makeContainer(displayer, IEditor.Utils.noEditor(), resourceGetter, from, "entity", imageAnchor, key);
				BindingContext bindingContext = new BindingContext(RepositoryDataItemStatus.FOUND, "someUrl", Maps.<String, Object> makeMap(key, data), Maps.<String, Object> makeMap(RepositoryConstants.entity, "entity"));
				container.setValues(bindingContext);
				return container.getComposite();
			}
		});
	}

	public static IDisplayContainer makeContainerForKey1(final IDisplayer<?, ?> displayer, IEditor editor, final IResourceGetter resourceGetter, Composite from) {
		return makeContainer(displayer, editor, resourceGetter, from, "entity", Displayers.class, "Key1");
	}

	public static IDisplayContainer makeContainer(final IDisplayer<?, ?> displayer, IEditor editor, final IResourceGetter resourceGetter, Composite from, String entity, Class<?> imageAnchor, String key) {
		Display display = from.getDisplay();
		ImageRegistry imageRegistry = IImageRegister.Utils.withBasics(display);
		Images.registerImages(from.getDisplay(), imageRegistry, imageAnchor, key);
		// Images.registerImages(from.getDisplay(), imageRegistry, Displayers.class, "Key1");
		IUrlGeneratorMap urlGeneratorMap = IUrlGeneratorMap.Utils.urlGeneratorMap();
		DisplayerContext context = new DisplayerContext(//
				ISelectedBindingManager.Utils.noSelectedBindingManager(), //
				ISoftwareFmRepository.Utils.repository(), //
				urlGeneratorMap,//
				ConfigForTitleAnd.create(display, resourceGetter, imageRegistry));

		IDisplayContainerFactoryBuilder factoryBuilder = IDisplayContainerFactoryBuilder.Utils.factoryBuilder(null);
		factoryBuilder.registerDisplayer("displayer", displayer);
		factoryBuilder.registerEditor("editor", editor);
		IDisplayContainerFactory factory = factoryBuilder.build();
		factory.register(Maps.<String, String> makeMap(//
				RepositoryConstants.entity, entity,//
				DisplayCoreConstants.key, key, //
				DisplayCoreConstants.displayer, "displayer", //
				DisplayCoreConstants.editor, "editor", //
				DisplayCoreConstants.smallImageKey, ArtifactsAnchor.documentKey));

		IDisplayContainer container = factory.create(context, from);
		return container;
	}
}
