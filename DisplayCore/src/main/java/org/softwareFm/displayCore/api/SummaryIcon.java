package org.softwareFm.displayCore.api;

import java.text.MessageFormat;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.swtBasics.images.ImageButton;

public class SummaryIcon extends ImageButton {

	private final DisplayerDetails displayerDetails;

	public SummaryIcon(Composite parent, ImageRegistry imageRegistry, DisplayerDetails displayerDetails, boolean toggle) {
		super(parent, imageRegistry, getSmallImageKey(displayerDetails), toggle);
		this.displayerDetails = displayerDetails;
	}

	public boolean matches(BindingContext bindingContext) {
		return displayerDetails.entity.equals(bindingContext.context.get(RepositoryConstants.entity));
	}

	private static String getSmallImageKey(DisplayerDetails displayerDetails) {
		String smallImageKey = displayerDetails.getSmallImageKey();
		if (smallImageKey == null)
			throw new IllegalArgumentException(MessageFormat.format(DisplayCoreConstants.smallImageKeyMissing, displayerDetails.key, displayerDetails.map));
		return smallImageKey;
	}
}
