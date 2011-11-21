package org.softwareFm.card.navigation.internal;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class NavIconFunction implements IFunction1<Map<String, Object>, Image> {
	private final IFunction1<String, Image> imageFn;
	private final IFunction1<String, IResourceGetter> resourceGetterFn;

	public NavIconFunction(IFunction1<String, IResourceGetter> resourceGetterFn, IFunction1<String, Image> imageFn) {
		this.imageFn = imageFn;
		this.resourceGetterFn = resourceGetterFn;
	}

	@Override
	public Image apply(Map<String, Object> from) throws Exception {
		String cardType = (String) from.get("sling:resourceType");
		String iconName = IResourceGetter.Utils.getOrNull(resourceGetterFn, cardType, CardConstants.navIcon);
		return Functions.call(imageFn, iconName);
	}
}