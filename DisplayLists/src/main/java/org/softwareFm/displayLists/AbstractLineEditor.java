package org.softwareFm.displayLists;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.ICodec;
import org.softwareFm.displayCore.api.ILineEditable;
import org.softwareFm.displayCore.api.ILineEditor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.swtBasics.images.IImageButtonListener;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.ImageButtons;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.swtBasics.text.IButtonParent;

public abstract class AbstractLineEditor<T, Line extends IButtonParent> implements ILineEditor<T> {

	private final ICodec<T> codec;

	public AbstractLineEditor(ICodec<T> encoder) {
		super();
		this.codec = encoder;
	}

	protected void addButtons(final ILineEditable<T> lineEditable, Composite parent, final int index, Line buttonParent) {
		ImageButtons.addEditButton(buttonParent, SwtBasicConstants.editKey, OverlaysAnchor.editKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				edit(lineEditable, index);
			}

		});
		ImageButtons.addRowButton(buttonParent, SwtBasicConstants.deleteKey, SwtBasicConstants.deleteKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				lineEditable.getModel().delete(index);
				lineEditable.sendDataToServer();

			}
		});
		String key = lineEditable.getDisplayerDetails().key;
		ImageButtons.addHelpButton(buttonParent, Resources.getRowKey(key));
	}

	@Override
	public ICodec<T> getCodec() {
		return codec;
	}
}
