package org.softwarefm.display.displayer;

import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.images.IImageButtonListener;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.composites.TitleAndText;
import org.softwarefm.display.impl.DisplayerDefn;
import org.softwarefm.display.smallButtons.SimpleImageButton;

public class TextDisplayer implements IDisplayer {

	private final boolean mutable;

	public TextDisplayer(boolean mutable) {
		this.mutable = mutable;
	}

	@Override
	public IHasControl create(Composite parent, final DisplayerDefn defn, int style, CompositeConfig compositeConfig) {
		TitleAndText titleAndText = new TitleAndText(compositeConfig, parent, defn.title, true);
		titleAndText.setGlobalEdit(mutable);
		if (mutable){
			SimpleImageButton button = new SimpleImageButton(titleAndText.getButtonComposite(),  compositeConfig.imageButtonConfig.withImage(ArtifactsAnchor.documentKey, OverlaysAnchor.editKey));
			// button.setSize(new Point(35, 12));
			button.addListener(new IImageButtonListener() {
				@Override
				public void buttonPressed(ImageButton button) {
					System.out.println("Edit pressed");
				}
			});
			RowData data = new RowData();
			data.height = compositeConfig.imageButtonConfig.layout.smallButtonHeight;
			data.width = compositeConfig.imageButtonConfig.layout.smallButtonWidth;
			button.getControl().setLayoutData(data);
			titleAndText.buttonAdded();
			return button;

		}
		return titleAndText;
	}

}
