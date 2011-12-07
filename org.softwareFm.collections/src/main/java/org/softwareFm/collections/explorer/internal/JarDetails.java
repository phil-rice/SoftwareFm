package org.softwareFm.collections.explorer.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.resources.IResourceGetter;

public class JarDetails implements IHasControl {

	private final Text content;

	public JarDetails(Composite parent, IResourceGetter resourceGetter, final Runnable whenClicked) {
		content = new Text(parent, SWT.WRAP | SWT.V_SCROLL);
		content.setText(IResourceGetter.Utils.getOrException(resourceGetter, CollectionConstants.jarNotRecognised));
		content.setEditable(false);
		content.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				whenClicked.run();
			}
		})
;	}

	@Override
	public Control getControl() {
		return content;
	}

}
