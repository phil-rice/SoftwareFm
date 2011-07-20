package org.arc4eclipse.panel;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseCallback;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.binding.path.JavaElementRipper;
import org.arc4eclipse.binding.path.JavaElementRipperResult;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class SelectedArtefactPanel extends Composite implements IArc4EclipseCallback<IJarData> {

	private final OrganisationPanel organisationPanel;
	private final ProjectPanel projectPanel;
	private final IArc4EclipseRepository repository;
	private final DebugPanel debugPanel;
	private final JarPanel jarPanel;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param repository
	 */
	public SelectedArtefactPanel(Composite parent, int style, IArc4EclipseRepository repository) {
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
		tabItem4.setText("Jar");
		jarPanel = new JarPanel(tabFolder, SWT.BORDER, repository);
		tabItem4.setControl(jarPanel);

		TabItem tabItem5 = new TabItem(tabFolder, SWT.NULL);
		tabItem5.setText("Debug");
		debugPanel = new DebugPanel(tabFolder, SWT.BORDER, repository);
		tabItem5.setControl(debugPanel);
		repository.addLogger(debugPanel);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public static void main(String args[]) {
		final IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();
		Swts.display("Selected Artefact Panel", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				return new SelectedArtefactPanel(from, SWT.NULL, repository);
			}
		});
	}

	@Override
	public void setData(Object arg0) {
		super.setData(arg0);
		if (arg0 instanceof IBinding) {
			IBinding binding = (IBinding) arg0;
			JavaElementRipperResult ripperResult = JavaElementRipper.rip(binding);
			IPath path = ripperResult.path;
			if (path == null) {
				organisationPanel.clearBoundChildren();
				projectPanel.clearBoundChildren();
				jarPanel.clearBoundChildren();
			} else {
				jarPanel.clearBoundChildren();
				repository.getJarData(path.toFile(), this);
			}
		}

	}

	@Override
	public void process(IResponse response, IJarData jarData) {
		jarPanel.process(response, jarData);
		IUrlGenerator generator = repository.generator();
		if (jarData == null) {
			organisationPanel.clearBoundChildren();
			projectPanel.clearBoundChildren();

		} else {
			String organisationUrl = jarData.getOrganisationUrl();
			repository.getData(generator.forOrganisation(organisationUrl), IArc4EclipseRepository.Utils.organisationData(), organisationPanel);
			repository.getData(generator.forProject(organisationUrl, jarData.getProjectName()), IArc4EclipseRepository.Utils.projectData(), projectPanel);
		}
	}
}
