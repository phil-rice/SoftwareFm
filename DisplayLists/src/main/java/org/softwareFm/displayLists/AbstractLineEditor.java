package org.softwareFm.displayLists;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.ICodec;
import org.softwareFm.displayCore.api.ILineEditable;
import org.softwareFm.displayCore.api.ILineEditor;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.softwareFmImages.ImageButtons;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.images.IImageButtonListener;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.swtBasics.text.IButtonParent;

public abstract class AbstractLineEditor<T, Line extends IButtonParent> implements ILineEditor<T> {

	private final ICodec<T> codec;

	public AbstractLineEditor(ICodec<T> encoder) {
		super();
		this.codec = encoder;
	}

	protected void addButtons(final ILineEditable<T> lineEditable, Composite parent, final int index, Line buttonParent) {
		String key = lineEditable.getDisplayerDetails().map.get(DisplayCoreConstants.smallImageKey);
		ImageButtons.addEditButton(buttonParent, key, OverlaysAnchor.editKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				edit(lineEditable, index);
			}

		});
		ImageButtons.addRowButton(buttonParent, key, OverlaysAnchor.deleteKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				lineEditable.getModel().delete(index);
				lineEditable.sendDataToServer();

			}
		});
		ImageButtons.addHelpButton(buttonParent, Resources.getRowKey(lineEditable.getDisplayerDetails().key), GeneralAnchor.helpKey);
	}

	@Override
	public ICodec<T> getCodec() {
		return codec;
	}
}
