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
		final ClassAndMethodComposite classAndMethodComposite = new ClassAndMethodComposite(tabFolder, container);
//		final SoftwareFmComposite digestPanel = new DigestComposite(tabFolder, container);
		final LinkToProjectComposite linkToProjectComposite = new LinkToProjectComposite(tabFolder, container);
		final ProjectComposite projectComposite = new ProjectComposite(tabFolder, container);
		
//		final SoftwareFmComposite versionPanel = new VersionComposite(tabFolder, container);

		addTabItem(tabFolder, "Class And method", classAndMethodComposite.getControl());
//		addTabItem(tabFolder, "Digest", digestPanel.getControl());
		addTabItem(tabFolder, "Link to Project", linkToProjectComposite.getControl());
		addTabItem(tabFolder, "Project", projectComposite.getControl());
//		addTabItem(tabFolder, "Version", versionPanel.getControl());
	}

	private void addTabItem(TabFolder tabFolder, String title, Control control) {
		TabItem tabItem = new TabItem(tabFolder, SWT.BORDER);
		tabItem.setText(title);
		tabItem.setControl(control);
	}

}
