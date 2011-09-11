package org.softwareFm.displayCore.api;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.swtBasics.IHasControl;

public interface IDisplayer<L extends IHasControl, S extends IHasControl> {

	S createSmallControl(DisplayerContext displayerContext, IRegisteredItems registeredItems, ITopButtonState topButtonState, Composite parent, DisplayerDetails displayerDetails);

	L createLargeControl(Composite parent, DisplayerContext context, IRegisteredItems registeredItems, IDisplayContainerFactoryGetter displayContainerFactoryGetter, DisplayerDetails displayerDetails);

	void populateSmallControl(BindingContext bindingContext, S smallControl, Object value);

	void populateLargeControl(BindingContext bindingContext, L largeControl, Object value);

	static class Utils {
		public static boolean entitiesMatch(BindingContext bindingContext, String entity) {
			Object bindingEntity = bindingContext.context.get(RepositoryConstants.entity);
			return bindingEntity != null && bindingEntity.equals(entity);
		}
	}

}
