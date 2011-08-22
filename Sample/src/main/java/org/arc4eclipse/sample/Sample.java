package org.arc4eclipse.sample;

import java.io.IOException;
import java.io.InputStream;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactoryBuilder;
import org.arc4eclipse.displayForums.ForumDisplayer;
import org.arc4eclipse.displayJarPath.JarPathDisplayer;
import org.arc4eclipse.displayJavadoc.DisplayJavadoc;
import org.arc4eclipse.displayLists.ListDisplayer;
import org.arc4eclipse.displaySource.DisplaySource;
import org.arc4eclipse.displayText.TextDisplayer;
import org.arc4eclipse.displayUrl.UrlDisplayer;
import org.arc4eclipse.panel.ISelectedBindingManager;
import org.arc4eclipse.panel.SelectedArtefactPanel;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.springframework.core.io.FileSystemResource;

public class Sample {

	public static void main(String args[]) {
		final IImageFactory imageFactory = new IImageFactory() {
			@Override
			public Images makeImages(final Device device) {
				return new Images(device) {
					@Override
					protected Image makeImage(String string) {
						try {
							InputStream inputStream = new FileSystemResource(string).getInputStream();
							return new Image(device, inputStream);
						} catch (IOException e) {
							throw WrappedException.wrap(e);
						}
					}
				};
			}
		};
		Swts.display("Selected Artefact Panel", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				final IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();
				final DisplayerContext context = new DisplayerContext(imageFactory, ISelectedBindingManager.Utils.noSelectedBindingManager(), repository);
				IDisplayContainerFactoryBuilder builder = IDisplayContainerFactoryBuilder.Utils.factoryBuilder();
				builder.registerDisplayer(new TextDisplayer());
				builder.registerDisplayer(new JarPathDisplayer());
				builder.registerDisplayer(new UrlDisplayer());
				builder.registerDisplayer(new DisplaySource());
				builder.registerDisplayer(new DisplayJavadoc());
				builder.registerDisplayer(new ForumDisplayer());
				builder.registerDisplayer(new ListDisplayer());

				builder.registerForEntity("entity", RepositoryConstants.jarDetailsKey, "Jar Path", "Help for Jar Path");
				builder.registerForEntity("entity", "text_key1", "Some Text", "Help for some text");
				builder.registerForEntity("entity", RepositoryConstants.organisationUrlKey, "Organisation", "Help for organisationUrl");
				builder.registerForEntity("entity", RepositoryConstants.javadocKey, "Javadoc", "help for Javadoc");
				builder.registerForEntity("entity", RepositoryConstants.sourceKey, "Source", "help for source");
				builder.registerForEntity("entity", RepositoryConstants.projectUrlKey, "Project", "help for project");
				builder.registerForEntity("entity", RepositoryConstants.forumsKey, RepositoryConstants.forumsTitle, "help for forums");
				builder.registerForEntity("entity", RepositoryConstants.mailingListsKey, RepositoryConstants.mailingListsKey, "help for mailing list");
				IDisplayContainerFactory displayContainerFactory = builder.build();

				final SelectedArtefactPanel selectedArtefactPanel = new SelectedArtefactPanel(from, SWT.NULL, displayContainerFactory, context, "entity");
				from.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							selectedArtefactPanel.statusChanged("someUrl", RepositoryDataItemStatus.FOUND, Maps.<String, Object> makeMap(//
									"text_key1", "value1",//
									RepositoryConstants.forumsKey, new String[] { "User Forums$ww.forums.org/x", "Devs Forums$www.forums.org/y" }),//
									Maps.<String, Object> makeMap(RepositoryConstants.entity, "entity"));
						} catch (Exception e) {
							throw WrappedException.wrap(e);
						}
					}
				});

				return selectedArtefactPanel;
			}
		});
	}
}
