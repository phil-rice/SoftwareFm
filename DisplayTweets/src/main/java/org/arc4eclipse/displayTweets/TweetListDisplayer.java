package org.arc4eclipse.displayTweets;

import java.util.concurrent.Callable;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.displayLists.ListDisplayer;
import org.arc4eclipse.displayLists.ListPanel;
import org.arc4eclipse.displayLists.NameAndValue;
import org.arc4eclipse.swtBasics.images.ImageButtons;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class TweetListDisplayer extends ListDisplayer {

	@Override
	public ListPanel createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		return new ListPanel(parent, SWT.BORDER, context, displayerDetails) {
			@Override
			protected void addButtonsToList(final NameAndValue nameAndValue, TitleAndTextField text, int index) {
				ImageButtons.addBrowseButton(text, new Callable<String>() {

					@Override
					public String call() throws Exception {
						return nameAndValue.url;
					}
				});
				super.addButtonsToList(nameAndValue, text, index);
			}
		};
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, ListPanel largeControl, Object value) {
		largeControl.setValue(bindingContext.url, value);
	}

}