package org.softwarefm.eclipse.composite;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.UrlConstants;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.labelAndText.TextAndControlComposite;
import org.softwarefm.utilities.functions.IFunction1;

public class ProjectComposite extends TextAndControlComposite<StackedBrowserAndControl<LinkToProjectComposite>> {

	private StackedBrowserAndControl<LinkToProjectComposite> browserAndLinkToProject;
	private String url;

	public ProjectComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
	}

	@Override
	protected StackedBrowserAndControl<LinkToProjectComposite> makeComponent(final SoftwareFmContainer<?> container, Composite parent) {
		return browserAndLinkToProject = new StackedBrowserAndControl<LinkToProjectComposite>(parent, new IFunction1<Composite, LinkToProjectComposite>() {
			public LinkToProjectComposite apply(Composite from) throws Exception {
				return new LinkToProjectComposite(from, container);
			}
		});
	}

	@Override
	public void projectDetermined(ProjectData projectData, int selectionCount) {
		killLastLineAndappendText(projectDeterminedMsg(projectData));
		String url = urlStrategy.projectUrl(projectData).getHostAndUrl();
		setUrl(url);
	}

	@Override
	public void dispose() {
		super.dispose();
		browserAndLinkToProject.dispose();
	}

	void setUrl(String url) {
		this.url = url;
		browserAndLinkToProject.setUrlAndShow(url);
	}

	public String getUrl() {
		return url;
	}

	@Override
	public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
		setText(searchingMsg());
	}

	@Override
	public void notJavaElement(int selectionCount) {
		killLastLineAndappendText(notJavaElementMsg());
		setUrl(UrlConstants.notJavaElementUrl);
	}

	@Override
	public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		setText(notInAJarMsg(fileNameAndDigest));
		setUrl(UrlConstants.notJarUrl);
	}

	@Override
	public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		killLastLineAndappendText(digestDeterminedMsg(fileNameAndDigest) + "\n" + searchingMsg());
	}

	@Override
	public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		killLastLineAndappendText(unknownDigestMsg(fileNameAndDigest));
		browserAndLinkToProject.showSecondaryControl();
	}

	public static void main(String[] args) {
		Swts.Show.display(ProjectComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				ProjectComposite projectComposite = new ProjectComposite(from, SoftwareFmContainer.makeForTests());
				projectComposite.setUrl(UrlConstants.notJarUrl);
				return projectComposite.getComposite();
			}
		});
	}

}
