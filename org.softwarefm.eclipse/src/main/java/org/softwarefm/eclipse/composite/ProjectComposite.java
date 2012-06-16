package org.softwarefm.eclipse.composite;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.constants.UrlConstants;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.utilities.functions.IFunction1;

public class ProjectComposite extends StackedBrowserAndControl<LinkComposite> {


	public ProjectComposite(Composite parent, final SoftwareFmContainer<?> container) {
		super(parent, container, new IFunction1<Composite, LinkComposite>() {
			public LinkComposite apply(Composite from) throws Exception {
				return new LinkComposite(from, container);
			}
		});
		setUrlAndShow(UrlConstants.welcomeUrl);
		addListener(new ISelectedBindingListener() {
			public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
			}

			public void notJavaElement(int selectionCount) {
				setUrlAndShow(UrlConstants.notJavaElementUrl);
			}

			public void notInAJar(File file, int selectionCount) {
				setUrlAndShow(UrlConstants.notJarUrl);
			}

			public void digestDetermined(FileAndDigest fileAndDigest, int selectionCount) {
				setText(digestDeterminedMsg(TextKeys.msgProjectFoundDigest, fileAndDigest) + "\n" + searchingMsg());
			}

			public void unknownDigest(FileAndDigest fileAndDigest, int selectionCount) {
				showSecondaryControl();
			}

			public void projectDetermined(ProjectData projectData, int selectionCount) {
				String url = urlStrategy.projectUrl(projectData).getHostAndUrl();
				setUrlAndShow(url);
			}
		});
	}

	public static void main(String[] args) {
		Swts.Show.display(ProjectComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				ProjectComposite projectComposite = new ProjectComposite(from, SoftwareFmContainer.makeForTests());
				projectComposite.setUrlAndShow(UrlConstants.notJarUrl);
				return projectComposite.getComposite();
			}
		});
	}

}
