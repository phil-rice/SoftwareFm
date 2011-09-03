package org.arc4eclipse.displayJavadocAndSource;

import java.util.Map;
import java.util.concurrent.Callable;

import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.images.ImageButtons;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

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
				if (ripped != null) {
					System.out.println("Linking");
					try {
						IPackageFragmentRoot packageFragment = ripped.packageFragment;
						Path newValue = new Path(txtRepository.getText());
						packageFragment.attachSource(newValue, null, new NullProgressMonitor());
					} catch (JavaModelException e) {
						throw WrappedException.wrap(e);
					}
				}
			}
		});
		ImageButtons.addHelpButton(txtRepository, DisplaySourceConstants.repositoryKey);
		txtLocal = new TitleAndTextField(context.configForTitleAnd, this, DisplaySourceConstants.localKey);
		ImageButtons.addRowButton(txtLocal, SwtBasicConstants.clearKey, SourceConstants.clearHelpKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				System.out.println("Linking");
				try {
					IPackageFragmentRoot packageFragment = ripped.packageFragment;
					packageFragment.attachSource(null, new Path(""), new NullProgressMonitor());
				} catch (JavaModelException e) {
					throw WrappedException.wrap(e);
				}

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
