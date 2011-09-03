package org.softwareFm.displayJavadocAndSource;

import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.BoundTitleAndTextField;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.JavaProjects;
import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.IImageButtonListener;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.ImageButtons;
import org.softwareFm.swtBasics.text.TitleAndTextField;
import org.softwareFm.utilities.strings.Strings;

public class SourcePanel extends Composite {

	private final ImageButton btnAttach;
	private final TitleAndTextField txtLocal;
	private final BoundTitleAndTextField txtRepository;
	private BindingRipperResult ripped;

	public SourcePanel(Composite parent, DisplayerContext context, DisplayerDetails displayerDetails) {
		super(parent, context.configForTitleAnd.style);
		setLayout(new GridLayout());
		txtRepository = new BoundTitleAndTextField(this, context, displayerDetails.withKey(DisplaySourceConstants.repositoryKey));
		ImageButtons.addBrowseButton(txtRepository, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return txtRepository.getText();
			}
		});
		txtRepository.addEditButton();
		btnAttach = ImageButtons.addRowButton(txtRepository, DisplaySourceConstants.linkImageKey, DisplaySourceConstants.linkKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				JavaProjects.setSourceAttachment(ripped.javaProject, ripped.classpathEntry, txtRepository.getText());
			}

		});
		ImageButtons.addHelpButton(txtRepository, DisplaySourceConstants.repositoryKey);
		txtLocal = new TitleAndTextField(context.configForTitleAnd, this, DisplaySourceConstants.localKey);
		ImageButtons.addRowButton(txtLocal, SwtBasicConstants.clearKey, SourceConstants.clearHelpKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				JavaProjects.setSourceAttachment(ripped.javaProject, ripped.classpathEntry, null);
			}
		});
		ImageButtons.addHelpButton(txtLocal, DisplaySourceConstants.localKey);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
	}

	public void setValue(String url, BindingRipperResult ripped, Map<String, Object> data) {
		this.ripped = ripped;
		String repositoryValue = (String) (data == null ? null : data.get(DisplaySourceConstants.repositoryKey));
		txtRepository.setUrl(url);
		txtRepository.setText(Strings.nullSafeToString(repositoryValue));
		txtLocal.setText(Strings.nullSafeToString(ripped == null ? null : ripped.sourceAttachmentPath));
		ImageButton.Utils.setEnabledIfNotBlank(btnAttach, repositoryValue);
	}
}
