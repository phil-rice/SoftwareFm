package org.arc4eclipse.displayMainUrl;

import java.util.concurrent.Callable;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.Displayers;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.swtBasics.images.ImageButtons;
import org.arc4eclipse.swtBasics.images.Resources;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class MainUrlDisplayer extends AbstractDisplayerWithLabel<MainUrlPanel> {

	@Override
	public MainUrlPanel createLargeControl(final DisplayerContext context, final IRegisteredItems registeredItems, Composite parent, final DisplayerDetails displayerDetails) {
		final MainUrlPanel titleAndTextField = new MainUrlPanel(context.configForTitleAnd, parent, displayerDetails.key);
		titleAndTextField.setEditable(true);
		ImageButtons.addBrowseButton(titleAndTextField, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return titleAndTextField.getText();
			}
		});
		ImageButtons.addHelpButton(titleAndTextField, displayerDetails.key);
		titleAndTextField.addCrListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				final String entity = displayerDetails.entity;
				String rawUrl = titleAndTextField.getText();
				IUrlGenerator urlGenerator = context.urlGeneratorMap.get(entity);
				String url = urlGenerator.apply(rawUrl);
				context.repository.getData(entity, url, IArc4EclipseRepository.Utils.makeSecondaryContext(entity, displayerDetails.key, rawUrl));
			}
		});
		return titleAndTextField;
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, MainUrlPanel largeControl, Object value) {
		Object urlKey = bindingContext.context.get(RepositoryConstants.urlKey);
		if (urlKey == null)
			largeControl.setText("");
		else if (urlKey.equals(largeControl.key)) {
			Object rawUrl = bindingContext.context.get(RepositoryConstants.rawUrl);
			largeControl.setText(Strings.nullSafeToString(rawUrl));
		}
	}

	@Override
	public String toString() {
		return "MainUrlDisplayer";
	}

	public static void main(String[] args) {
		Displayers.displayWithKey1(new MainUrlDisplayer(), Resources.resourceGetterWithBasics("DisplayForTest"), "text");
	}
}