package org.arc4eclipse.displayCore.api;

import java.util.List;
import java.util.Map;

import org.arc4eclipse.displayCore.api.impl.DisplayContainer;
import org.arc4eclipse.swtBasics.IHasComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public interface IDisplayContainer extends IHasComposite {

	void addDisplayers(BindingContext bindingContext, String url, Map<String, Object> data, final List<NameSpaceNameValueAndDisplayer> toBeDisplayed);

	void dispose();

	public static class Utils {
		public static IDisplayContainer displayContainer(Composite parent) {
			return new DisplayContainer(parent, SWT.BORDER);
		}
	}

}
