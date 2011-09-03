package org.arc4eclipse.displayLists;

import org.arc4eclipse.displayCore.api.ICodec;
import org.arc4eclipse.displayCore.api.ILineEditable;
import org.arc4eclipse.displayCore.api.ILineEditor;
import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.images.ImageButtons;
import org.arc4eclipse.swtBasics.images.Resources;
import org.arc4eclipse.swtBasics.text.IButtonParent;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractLineEditor<T, Line extends IButtonParent> implements ILineEditor<T> {

	private final ICodec<T> codec;

	public AbstractLineEditor(ICodec<T> encoder) {
		super();
		this.codec = encoder;
	}

	protected void addButtons(final ILineEditable<T> lineEditable, Composite parent, final int index, Line buttonParent) {
		ImageButtons.addEditButton(buttonParent, new IImageButtonListener() {
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
