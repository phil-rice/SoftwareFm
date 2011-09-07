package org.softwareFm.scanView;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.jdtBinding.api.JavaProjects;
import org.softwareFm.softwareFmImages.ImageButtons;
import org.softwareFm.swtBasics.images.IImageButtonListener;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.text.AbstractTitleAnd;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.utilities.aggregators.IAggregator;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.functions.IFunction1;

public class ScanComposite extends AbstractTitleAnd {

	public ScanComposite(Composite parent, int style, ConfigForTitleAnd config) {
		super(config, parent, ScanViewConstants.scanKey, true);
		setLayout(new FormLayout());
		final StyledText txtText = new StyledText(this, SWT.BORDER);
		txtText.setLayoutData(new RowData(400, 400));

		FormData lblLayoutData = new FormData();
		lblLayoutData.top = new FormAttachment(0, 0);
		lblLayoutData.left = new FormAttachment(0, 0);
		compTitleAndButtons.setLayoutData(lblLayoutData);

		FormData txtLayoutData = new FormData();
		txtLayoutData.top = new FormAttachment(0, 31);
		txtLayoutData.left = new FormAttachment(0, 0);
		txtLayoutData.bottom = new FormAttachment(100, 0);
		txtLayoutData.right = new FormAttachment(100, 0);
		txtText.setLayoutData(txtLayoutData);

		ImageButtons.addRowButton(this, ScanViewConstants.scanKey, ScanViewConstants.scanKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				String txt = Iterables.aggregate(Iterables.map(JavaProjects.allProjects(), new IFunction1<IProject, String>() {
					@Override
					public String apply(IProject from) throws Exception {
						StringBuilder builder = new StringBuilder();
						builder.append(from.getName() + "\n");
						IJavaProject javaProject = JavaCore.create(from);
						if (javaProject.isOpen()) {
							IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();

							for (IPackageFragmentRoot root : roots) {
								builder.append("  Root: " + root.getPath() + "\n");
								IPath source = root.getSourceAttachmentPath();
								IClasspathEntry resolved = root.getResolvedClasspathEntry();
								builder.append("    Source:  " + source + "\n");
								builder.append("    Javadoc: " + JavaProjects.findJavadocFor(resolved) + "\n");

							}
						}
						return builder.toString();
					}
				}), IAggregator.Factory.<String> join("\n"));
				txtText.setText(txt);
			}
		});
	}
}
