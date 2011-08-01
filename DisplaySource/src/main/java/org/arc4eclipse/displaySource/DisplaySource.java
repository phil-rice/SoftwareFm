package org.arc4eclipse.displaySource;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplaySource implements IDisplayer {

	@Override
	public String getNameSpace() {
		return RepositoryConstants.sourceKey;
	}

	@Override
	public Control makeCompositeAsChildOf(Composite parent, final BindingContext bindingContext, final NameSpaceNameAndValue nameSpaceNameAndValue) {
		try {
			Composite composite = new Composite(parent, SWT.BORDER);
			composite.setLayout(new GridLayout());
			final TitleAndTextField txtRepository = new BoundTitleAndTextField(composite, SWT.NULL, bindingContext, nameSpaceNameAndValue);
			final TitleAndTextField txtLocal = new TitleAndTextField(composite, SWT.NULL, bindingContext.images, "Current setting", false);
			Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
			final BindingRipperResult ripperResult = (BindingRipperResult) bindingContext.context.get(DisplayCoreConstants.ripperResult);
			if (ripperResult != null) {
				txtLocal.setText("");
				IPackageFragmentRoot root = ripperResult.packageFragment;
				if (root != null) {
					IPath sourceAttachmentPath = root.getSourceAttachmentPath();
					if (sourceAttachmentPath != null)
						txtLocal.setText(sourceAttachmentPath.toString());
				}
			}

			ImageButton btnAttach = txtRepository.addButton(bindingContext.images.getLinkImage(), "Attach", new IImageButtonListener() {
				@Override
				public void buttonPressed(ImageButton button) {
					try {
						if (ripperResult != null) {
							IPackageFragmentRoot root = ripperResult.packageFragment;
							root.attachSource(new Path(txtRepository.getText()), ripperResult.path, new NullProgressMonitor());
						}
					} catch (JavaModelException e1) {
						throw WrappedException.wrap(e1);
					}
				}
			});
			btnAttach.setEnabled(!txtRepository.getText().equals(""));
			return txtRepository;
		} catch (JavaModelException e) {
			throw WrappedException.wrap(e);
		}
	}
}