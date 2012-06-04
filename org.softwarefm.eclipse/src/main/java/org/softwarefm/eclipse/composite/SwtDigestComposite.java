package org.softwarefm.eclipse.composite;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.SelectedBindingAdapter;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.labelAndText.IButtonConfigurator;
import org.softwarefm.labelAndText.TextAndFormComposite;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.runnable.Runnables;

public class SwtDigestComposite extends TextAndFormComposite {

	public SwtDigestComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		addListener(new SelectedBindingAdapter() {

			@Override
			public void projectDetermined(ProjectData projectData, int selectionCount) {
				setText(SwtConstants.groupIdKey, projectData.groupId);
				setText(SwtConstants.artifactIdKey, projectData.artefactId);
				setText(SwtConstants.versionKey, projectData.version);
				setEnabledForButton(SwtConstants.okButton, false);
			}
		});
	}

	@Override
	protected IButtonConfigurator makeButtonConfigurator() {
		return IButtonConfigurator.Utils.ok(Runnables.sysout("ok"));
	}

	@Override
	protected String[] makeKeys() {
		return new String[] { SwtConstants.groupIdKey, SwtConstants.artifactIdKey, SwtConstants.versionKey };
	}

	@Override
	public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		killLastLineAndappendText(unknownDigestMsg(fileNameAndDigest));
		setText(SwtConstants.groupIdKey, "Enter Group Id");
		setText(SwtConstants.artifactIdKey, "Enter Artifact Id");
		setText(SwtConstants.versionKey, "Enter Version Id");
	}

	@Override
	public void projectDetermined(ProjectData projectData, int selectionCount) {
		killLastLineAndappendText(projectDeterminedMsg(projectData) + "\nIf you think this is linked to the wrong data, follow <these instructions>");
		setText(SwtConstants.groupIdKey, projectData.groupId);
		setText(SwtConstants.artifactIdKey, projectData.artefactId);
		setText(SwtConstants.versionKey, projectData.version);
	}

	private void clearForm() {
		setText(SwtConstants.groupIdKey, "<Not Relevant>");
		setText(SwtConstants.artifactIdKey, "<Not Relevant>");
		setText(SwtConstants.versionKey, "<Not Relevant>");
	}

	@Override
	protected void notJavaElement(int selectionCount) {
		killLastLineAndappendText(notJavaElementMsg());
		clearForm();
	}

	@Override
	public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		killLastLineAndappendText(notInAJarMsg(fileNameAndDigest));
	}

	@Override
	public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
		setText(searchingMsg());
	}

	@Override
	public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		killLastLineAndappendText(digestDeterminedMsg(fileNameAndDigest) + "\n" + searchingMsg());
	}

	public static void main(String[] args) {
		Swts.Show.display(SwtDigestComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				SoftwareFmContainer<Object> container = SoftwareFmContainer.makeForTests();
				return new SwtDigestComposite(from, container).getComposite();
			}
		});
	}
}
