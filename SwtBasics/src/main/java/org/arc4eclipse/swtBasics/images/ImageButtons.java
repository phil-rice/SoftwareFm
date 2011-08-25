package org.arc4eclipse.swtBasics.images;

import java.awt.Desktop;
import java.net.URI;
import java.text.MessageFormat;
import java.util.concurrent.Callable;

import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.swtBasics.text.IButtonParent;
import org.arc4eclipse.utilities.exceptions.Exceptions;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.arc4eclipse.utilities.strings.Urls;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Display;

abstract public class ImageButtons {
	public static ImageButton addRowButton(IButtonParent parent, String imageKey, String tooltipKey, final IImageButtonListener listener) {
		return addRowButton(parent, imageKey, SwtBasicConstants.tooltipPattern, tooltipKey, listener);
	}

	public static ImageButton addBrowseButton(IButtonParent parent, final Callable<String> urlGetter) {
		return addRowButton(parent, SwtBasicConstants.browseKey, SwtBasicConstants.browseKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				try {
					URI uri = Urls.withDefaultProtocol("http", urlGetter.call());
					Desktop.getDesktop().browse(uri);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
	}

	private static ImageButton addRowButton(IButtonParent parent, String imageKey, String tooltipPattern, String tooltipKey, final IImageButtonListener listener) {
		ImageButton button = new ImageButton(parent.getButtonComposte(), parent.getImageRegistry(), imageKey, false);
		// button.setSize(new Point(35, 12));
		button.addListener(new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				try {
					listener.buttonPressed(button);
				} catch (Exception e) {
					Status status = new Status(IStatus.ERROR, "My Plug-in ID", 0, Exceptions.stackTraceString(e.getStackTrace(), "\n"), null);
					ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Exception pressing button", e.getMessage(), status);
					throw WrappedException.wrap(e);
				}
			}
		});
		String fullKey = MessageFormat.format(tooltipPattern, tooltipKey);
		button.setTooltipText(IResourceGetter.Utils.get(parent.getResourceGetter(), fullKey));
		RowData data = new RowData();
		data.height = 18;
		data.width = 18;
		button.setLayoutData(data);
		parent.buttonAdded();
		return button;
	}

	public static ImageButton addHelpButton(final IButtonParent parent, final String helpKey) {
		final IResourceGetter resourceGetter = parent.getResourceGetter();
		ImageButton helpButton = addRowButton(parent, SwtBasicConstants.helpKey, SwtBasicConstants.summaryHelpPattern, helpKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				String detailed = Resources.getDetailedHelp(resourceGetter, helpKey);
				if (detailed != null)
					MessageDialog.openInformation(parent.getButtonComposte().getShell(), "Help", Resources.getDetailedHelp(resourceGetter, helpKey));
			}
		});
		return helpButton;
	}

	public static void addEditButton(IButtonParent parent, IImageButtonListener listener) {
		addRowButton(parent, SwtBasicConstants.key, SwtBasicConstants.key, listener);
	}
}
