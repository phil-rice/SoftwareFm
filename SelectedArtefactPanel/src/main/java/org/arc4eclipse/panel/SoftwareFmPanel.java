package org.arc4eclipse.panel;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class SoftwareFmPanel extends Composite {

	private final OrganisationPanel organisationPanel;
	private final ProjectPanel projectPanel;
	private final ReleasePanel releasePanel;
	private final IArc4EclipseRepository repository;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param repository
	 */
	public SoftwareFmPanel(Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style);
		this.repository = repository;
		setLayout(new FillLayout(SWT.HORIZONTAL));

		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
		tabItem.setText("My Software FM");
		tabItem.setControl(new MySoftwareFmPanel(tabFolder, SWT.BORDER));
		TabItem tabItem2 = new TabItem(tabFolder, SWT.NULL);
		tabItem2.setText("Organisation");
		organisationPanel = new OrganisationPanel(tabFolder, SWT.BORDER, repository);
		tabItem2.setControl(organisationPanel);
		TabItem tabItem3 = new TabItem(tabFolder, SWT.NULL);
		tabItem3.setText("Project");
		projectPanel = new ProjectPanel(tabFolder, SWT.BORDER, repository);
		tabItem3.setControl(projectPanel);
		TabItem tabItem4 = new TabItem(tabFolder, SWT.NULL);
		tabItem4.setText("Release");
		releasePanel = new ReleasePanel(tabFolder, SWT.BORDER);
		tabItem4.setControl(releasePanel);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setData(Object arg0) {
		super.setData(arg0);
		if (arg0 instanceof IJarData) {
			IJarData jarData = (IJarData) arg0;
			IUrlGenerator generator = repository.generator();
			String organisationUrl = jarData.getOrganisationUrl();
			repository.getData(generator.forOrganisation(organisationUrl), IArc4EclipseRepository.Utils.organisationData(), organisationPanel);
			repository.getData(generator.forProject(organisationUrl, jarData.getProjectName()), IArc4EclipseRepository.Utils.projectData(), projectPanel);

		}
	}

	public static void main(String args[]) {
		final IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();
		Swts.display("Selected Artefact Panel", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				return new SoftwareFmPanel(from, SWT.NULL, repository);
			}
		});
	}
}
