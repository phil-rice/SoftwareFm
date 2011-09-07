package org.softwareFm.displayMainUrl;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.displayCore.api.AbstractDisplayerWithLabel;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.Displayers;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.repository.api.ISoftwareFmRepository;
import org.softwareFm.repository.api.IUrlGenerator;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.swtBasics.images.ImageButtons;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.strings.Strings;

public class MainUrlDisplayer extends AbstractDisplayerWithLabel<MainUrlPanel> {

	@Override
	public MainUrlPanel createLargeControl(final DisplayerContext context, final IRegisteredItems registeredItems, Composite parent, final DisplayerDetails displayerDetails) {
		final MainUrlPanel titleAndTextField = new MainUrlPanel(context.configForTitleAnd, parent, displayerDetails.key);
		titleAndTextField.setEditable(true);
		ImageButtons.addBrowseButton(titleAndTextField, GeneralAnchor.browseKey, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return titleAndTextField.getText();
			}
		});
		ImageButtons.addHelpButton(titleAndTextField, displayerDetails.key, GeneralAnchor.helpKey);
		titleAndTextField.addCrListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				final String entity = displayerDetails.entity;
				String rawUrl = titleAndTextField.getText();
				IUrlGenerator urlGenerator = context.urlGeneratorMap.get(entity);
				String url = urlGenerator.apply(rawUrl);
				context.repository.getData(entity, url, ISoftwareFmRepository.Utils.makeSecondaryContext(entity, displayerDetails.key, rawUrl));
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