package org.softwareFm.card.navigation;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.navigation.internal.NavIconFunction;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public interface ITitleBarForCard extends IHasControl {

	void setUrl(ICard card);

	public static class Utils {
		public static IFunction1<Map<String, Object>, Image> navIconFn(IFunction1<String, IResourceGetter> resourceGetterFn, IFunction1<String, Image> imageFn) {
			return new NavIconFunction(resourceGetterFn, imageFn);
		}
	}
}
