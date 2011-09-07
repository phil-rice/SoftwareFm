package org.softwareFm.swtBasics.images;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.text.MessageFormat;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.exceptions.Exceptions;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Urls;

abstract public class ImageButtons {
	public static ImageButton addRowButton(IButtonParent parent, String imageKey, String tooltipKey, final IImageButtonListener listener) {
		return addRowButton(parent, imageKey, null, SwtBasicConstants.tooltipPattern, tooltipKey, listener);
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

	private static ImageButton addRowButton(IButtonParent parent, String imageKey, String overlayKey, String tooltipPattern, String tooltipKey, final IImageButtonListener listener) {
		ImageButton button = new ImageButton(parent.getButtonComposite(), parent.getImageRegistry(), imageKey, overlayKey, false);
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
		if (tooltipKey != null) {
			String fullKey = MessageFormat.format(tooltipPattern, tooltipKey);
			button.setTooltipText(IResourceGetter.Utils.get(parent.getResourceGetter(), fullKey));
		}
		RowData data = new RowData();
		data.height = 18;
		data.width = 18;
		button.setLayoutData(data);
		parent.buttonAdded();
		return button;
	}

	public static ImageButton addHelpButton(final IButtonParent parent, final String helpKey) {
		final IResourceGetter resourceGetter = parent.getResourceGetter();
		ImageButton helpButton = addRowButton(parent, SwtBasicConstants.helpKey, null, SwtBasicConstants.summaryHelpPattern, helpKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				String detailed = Resources.getDetailedHelp(resourceGetter, helpKey);
				if (detailed != null)
					MessageDialog.openInformation(parent.getButtonComposite().getShell(), "Help", Resources.getDetailedHelp(resourceGetter, helpKey));
			}
		});
		return helpButton;
	}

	public static ImageButton addEditButton(IButtonParent parent, String imageKey, String overlayKey, IImageButtonListener listener) {
		return addRowButton(parent, imageKey, overlayKey, SwtBasicConstants.tooltipPattern, SwtBasicConstants.editKey, listener);
	}

	public static ImageButton addOpenFolderButton(IButtonParent buttonParent, String tooltipKey, final Callable<File> callable) {
		return addRowButton(buttonParent, SwtBasicConstants.folderKey, tooltipKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) throws Exception {
				Desktop.getDesktop().open(callable.call());
			}
		});
	}

	public static void setSmallIcon(SmallIconPosition pos, String key, ImageButton... buttons) {
		for (ImageButton imageButton : buttons)
			imageButton.setSmallIcon(pos, key);
	}

	public static void clearSmallIcons(ImageButton... buttons) {
		for (ImageButton imageButton : buttons)
			imageButton.clearSmallIcons();

	}
}
