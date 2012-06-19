package org.softwarefm.eclipse.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.softwarefm.eclipse.SoftwareFmContainer;

public class AllComposite extends SoftwareFmComposite {

	public AllComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		getComposite().setLayout(new FillLayout());
		TabFolder tabFolder = new TabFolder(getComposite(), SWT.NULL);
		final CodeComposite codeComposite = new CodeComposite(tabFolder, container);
//		final SoftwareFmComposite digestPanel = new DigestComposite(tabFolder, container);
		final LinkComposite linkComposite = new LinkComposite(tabFolder, container);
		final ArtifactComposite artifactComposite = new ArtifactComposite(tabFolder, container);
		
//		final SoftwareFmComposite versionPanel = new VersionComposite(tabFolder, container);

		addTabItem(tabFolder, "Code", codeComposite.getControl());
//		addTabItem(tabFolder, "Digest", digestPanel.getControl());
		addTabItem(tabFolder, "Link", linkComposite.getControl());
		addTabItem(tabFolder, "Artifact", artifactComposite.getControl());
//		addTabItem(tabFolder, "Version", versionPanel.getControl());
	}

	private void addTabItem(TabFolder tabFolder, String title, Control control) {
		TabItem tabItem = new TabItem(tabFolder, SWT.BORDER);
		tabItem.setText(title);
		tabItem.setControl(control);
	}

}
