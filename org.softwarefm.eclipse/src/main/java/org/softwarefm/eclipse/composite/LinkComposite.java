package org.softwarefm.eclipse.composite;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.labelAndText.TextAndControlComposite;
import org.softwarefm.labelAndText.TextAndFormComposite;
import org.softwarefm.utilities.functions.IFunction1;

public class LinkComposite extends TextAndControlComposite<TabFolder> {

	private TextAndFormComposite manualImport;
	private MavenImportComposite mavenImport;
	TabFolder tabFolder;
	TabItem mavenImportTabItem;
	TabItem manualImportTabItem;

	public LinkComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
	}

	@Override
	public void classAndMethodSelectionOccured(CodeData codeData, int selectionCount) {
		setText(searchingMsg());
	}

	@Override
	public void notInAJar(File file, int selectionCount) {
		setText(notInAJarMsg(file));
	}

	@Override
	protected void notJavaElement(int selectionCount) {
		setText(notJavaElementMsg());
	}

	@Override
	public void artifactDetermined(ArtifactData artifactData, int selectionCount) {
		setText(artifactDeterminedMsg(TextKeys.msgLinkArtifactDetermined, artifactData));
		tabFolder.setSelection(manualImportTabItem);
	}

	@Override
	public void unknownDigest(FileAndDigest fileAndDigest, int selectionCount) {
		setText(unknownDigestMsg(TextKeys.msgLinkUnknownDigest, fileAndDigest));
		tabFolder.setSelection(mavenImportTabItem);
	}

	@Override
	protected TabFolder makeComponent(SoftwareFmContainer<?> container, Composite parent) {
		tabFolder = new TabFolder(parent, SWT.NULL);
		mavenImport = new MavenImportComposite(tabFolder, container);
		manualImport = new ManualImportComposite(tabFolder, container);

		mavenImportTabItem = new TabItem(tabFolder, SWT.NULL);
		mavenImportTabItem.setText("Maven Import");
		mavenImportTabItem.setControl(mavenImport.getControl());

		manualImportTabItem = new TabItem(tabFolder, SWT.NULL);
		manualImportTabItem.setText("Manual Import");
		manualImportTabItem.setControl(manualImport.getControl());
		return tabFolder;
	}

	@Override
	public void dispose() {
		manualImport.dispose();
		mavenImport.dispose();
		super.dispose();

	}

	public static void main(String[] args) {
		Swts.Show.display(LinkComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				SoftwareFmContainer<Object> container = SoftwareFmContainer.makeForTests(from.getDisplay());
				return new LinkComposite(from, container).getComposite();
			}
		});
	}

}
